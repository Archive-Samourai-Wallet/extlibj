package com.samourai.wallet.bip47;

import com.samourai.soroban.client.RpcWallet;
import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.bip47.rpc.NotSecp256k1Exception;
import com.samourai.wallet.bip47.rpc.PaymentAddress;
import com.samourai.wallet.bip47.rpc.PaymentCode;
import com.samourai.wallet.bip47.rpc.secretPoint.ISecretPointFactory;
import com.samourai.wallet.hd.HD_Address;
import com.samourai.wallet.segwit.SegwitAddress;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bouncycastle.util.encoders.Hex;

public abstract class BIP47UtilGeneric {

    private static ISecretPointFactory secretPointFactory;
    private static boolean secretPointFactoryForced;

    protected BIP47UtilGeneric(ISecretPointFactory secretPointFactory, boolean secretPointFactoryForced) {
        if (BIP47UtilGeneric.secretPointFactory == null || !BIP47UtilGeneric.secretPointFactoryForced) {
            BIP47UtilGeneric.secretPointFactory = secretPointFactory;
        }
        if (secretPointFactoryForced) {
            // avoids Android impl getting overriden by Java impl
            BIP47UtilGeneric.secretPointFactoryForced = true;
        }
    }
    protected BIP47UtilGeneric(ISecretPointFactory secretPointFactory) {
        this(secretPointFactory, false);
    }

    public HD_Address getNotificationAddress(BIP47Wallet wallet) {
        return wallet.getAccount(0).getNotificationAddress();
    }

    public HD_Address getNotificationAddress(BIP47Wallet wallet, int account) {
        return wallet.getAccount(account).getNotificationAddress();
    }

    public com.samourai.wallet.bip47.rpc.PaymentCode getPaymentCode(BIP47Wallet wallet) throws AddressFormatException   {
        String payment_code = wallet.getAccount(0).getPaymentCode();
        return new com.samourai.wallet.bip47.rpc.PaymentCode(payment_code);
    }

    public com.samourai.wallet.bip47.rpc.PaymentCode getPaymentCode(BIP47Wallet wallet, int account) throws AddressFormatException   {
        String payment_code = wallet.getAccount(account).getPaymentCode();
        return new com.samourai.wallet.bip47.rpc.PaymentCode(payment_code);
    }

    public com.samourai.wallet.bip47.rpc.PaymentCode getFeaturePaymentCode(BIP47Wallet wallet) throws AddressFormatException   {
        PaymentCode payment_code = getPaymentCode(wallet);
        return new com.samourai.wallet.bip47.rpc.PaymentCode(payment_code.makeSamouraiPaymentCode());
    }

    public com.samourai.wallet.bip47.rpc.PaymentCode getFeaturePaymentCode(BIP47Wallet wallet, int account) throws AddressFormatException   {
        PaymentCode payment_code = getPaymentCode(wallet, account);
        return new com.samourai.wallet.bip47.rpc.PaymentCode(payment_code.makeSamouraiPaymentCode());
    }

    public SegwitAddress getReceiveAddress(BIP47Wallet wallet, PaymentCode pcode, int idx, NetworkParameters params) throws Exception {
        return getReceiveAddress(wallet, 0, pcode, idx, params);
    }

    public SegwitAddress getReceiveAddress(RpcWallet rpcWallet, PaymentCode paymentCodeCounterparty, int idx, NetworkParameters params) throws Exception {
        BIP47Wallet bip47Wallet = rpcWallet.getBip47Wallet();
        int bip47Account = rpcWallet.getBip47Account().getId();
        return getReceiveAddress(bip47Wallet, bip47Account, paymentCodeCounterparty, idx, params);
    }

    public SegwitAddress getReceiveAddress(BIP47Wallet wallet, int account, PaymentCode pcode, int idx, NetworkParameters params) throws Exception {
        HD_Address address = wallet.getAccount(account).addressAt(idx);
        return getPaymentAddress(pcode, 0, address, params).getSegwitAddressReceive();
    }

    public String getReceivePubKey(BIP47Wallet wallet, PaymentCode pcode, int idx, NetworkParameters params) throws Exception {
        return getReceivePubKey(wallet, 0, pcode, idx, params);
    }

    public String getReceivePubKey(BIP47Wallet wallet, int account, PaymentCode pcode, int idx, NetworkParameters params) throws Exception {
        ECKey ecKey = getReceiveAddress(wallet, account, pcode, idx, params).getECKey();
        return Hex.toHexString(ecKey.getPubKey());
    }

    public SegwitAddress getSendAddress(BIP47Wallet wallet, PaymentCode pcode, int idx, NetworkParameters params) throws Exception {
        return getSendAddress(wallet, 0, pcode, idx, params);
    }

    public SegwitAddress getSendAddress(RpcWallet rpcWallet, PaymentCode pcode, int idx, NetworkParameters params) throws Exception {
        BIP47Wallet bip47Wallet = rpcWallet.getBip47Wallet();
        int bip47Account = rpcWallet.getBip47Account().getId();
        return getSendAddress(bip47Wallet, bip47Account, pcode, idx, params);
    }

    public SegwitAddress getSendAddress(BIP47Wallet wallet, int account, PaymentCode pcode, int idx, NetworkParameters params) throws Exception {
        HD_Address address = wallet.getAccount(account).addressAt(0);
        return getPaymentAddress(pcode, idx, address, params).getSegwitAddressSend();
    }

    public String getSendPubKey(BIP47Wallet wallet, PaymentCode pcode, int idx, NetworkParameters params) throws Exception {
        return getSendPubKey(wallet, 0, pcode, idx, params);
    }

    public String getSendPubKey(BIP47Wallet wallet, int account, PaymentCode pcode, int idx, NetworkParameters params) throws Exception {
        ECKey ecKey = getSendAddress(wallet, account, pcode, idx, params).getECKey();
        return Hex.toHexString(ecKey.getPubKey());
    }

    public byte[] getIncomingMask(BIP47Wallet wallet, byte[] pubkey, byte[] outPoint, NetworkParameters params) throws Exception    {

        HD_Address notifAddress = getNotificationAddress(wallet);
        DumpedPrivateKey dpk = new DumpedPrivateKey(params, notifAddress.getPrivateKeyString());
        ECKey inputKey = dpk.getKey();
        byte[] privkey = inputKey.getPrivKeyBytes();
        byte[] mask = com.samourai.wallet.bip47.rpc.PaymentCode.getMask(secretPointFactory.newSecretPoint(privkey, pubkey).ECDHSecretAsBytes(), outPoint);

        return mask;
    }

    public byte[] getIncomingMask(BIP47Wallet wallet, int account, byte[] pubkey, byte[] outPoint, NetworkParameters params) throws AddressFormatException, Exception    {

        HD_Address notifAddress = getNotificationAddress(wallet, account);
        DumpedPrivateKey dpk = new DumpedPrivateKey(params, notifAddress.getPrivateKeyString());
        ECKey inputKey = dpk.getKey();
        byte[] privkey = inputKey.getPrivKeyBytes();
        byte[] mask = com.samourai.wallet.bip47.rpc.PaymentCode.getMask(secretPointFactory.newSecretPoint(privkey, pubkey).ECDHSecretAsBytes(), outPoint);

        return mask;
    }

    public PaymentAddress getPaymentAddress(PaymentCode pcode, int idx, HD_Address address, NetworkParameters params) throws AddressFormatException, NotSecp256k1Exception {
        DumpedPrivateKey dpk = new DumpedPrivateKey(params, address.getPrivateKeyString());
        ECKey eckey = dpk.getKey();
        PaymentAddress paymentAddress = new PaymentAddress(pcode, idx, eckey.getPrivKeyBytes(), params, secretPointFactory);
        return paymentAddress;
    }

}
