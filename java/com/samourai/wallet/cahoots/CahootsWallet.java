package com.samourai.wallet.cahoots;

import com.samourai.wallet.bip47.rpc.BIP47Account;
import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.bip47.rpc.PaymentCode;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.bipWallet.WalletSupplier;
import com.samourai.wallet.chain.ChainSupplier;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.send.provider.CahootsUtxoProvider;
import com.samourai.whirlpool.client.wallet.beans.SamouraiAccountIndex;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.bitcoinj.core.NetworkParameters;

import java.util.Collection;
import java.util.List;

public class CahootsWallet {
    private WalletSupplier walletSupplier;
    private BipFormatSupplier bipFormatSupplier;
    private ChainSupplier chainSupplier;
    private NetworkParameters params;
    private CahootsUtxoProvider utxoProvider;

    private HD_Wallet hdWallet;
    private BIP47Wallet bip47Wallet;

    public CahootsWallet(WalletSupplier walletSupplier, ChainSupplier chainSupplier, BipFormatSupplier bipFormatSupplier, NetworkParameters params, CahootsUtxoProvider utxoProvider) {
        this.walletSupplier = walletSupplier;
        this.chainSupplier = chainSupplier;
        this.bipFormatSupplier = bipFormatSupplier;
        this.params = params;
        this.utxoProvider = utxoProvider;

        this.hdWallet = walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84).getHdWallet();
        this.bip47Wallet = new BIP47Wallet(hdWallet);
    }

    private BipFormat likeTypedBipFormat(BipFormat bipFormat) {
        if (bipFormat == BIP_FORMAT.TAPROOT) {
            // like-typed output is not implemented for TAPROOT => handle TAPROOT mix output as SEGWIT_NATIVE
            return BIP_FORMAT.SEGWIT_NATIVE;
        }
        return bipFormat;
    }

    public BipWallet getReceiveWallet(int account, BipFormat bipFormat) throws Exception {
        bipFormat = likeTypedBipFormat(bipFormat);
        WhirlpoolAccount whirlpoolAccount = SamouraiAccountIndex.find(account);
        return walletSupplier.getWallet(whirlpoolAccount, bipFormat);
    }

    public BipAddress fetchAddressReceive(int account, boolean increment, BipFormat bipFormat) throws Exception {
        bipFormat = likeTypedBipFormat(bipFormat);
        return getReceiveWallet(account, bipFormat).getNextAddressReceive(bipFormat, increment);
    }

    public BipAddress fetchAddressChange(int account, boolean increment, BipFormat bipFormat) throws Exception {
        bipFormat = likeTypedBipFormat(bipFormat);
        return getReceiveWallet(account, bipFormat).getNextAddressChange(bipFormat, increment);
    }

    public BipFormatSupplier getBipFormatSupplier() {
        return bipFormatSupplier;
    }

    public ChainSupplier getChainSupplier() {
        return chainSupplier;
    }

    public NetworkParameters getParams() {
        return params;
    }

    public BIP47Wallet getBip47Wallet() {
        return bip47Wallet;
    }

    public int getBip47AccountIndex() {
        return 0;
    }

    public BIP47Account getBip47Account() {
        return bip47Wallet.getAccount(getBip47AccountIndex());
    }

    public PaymentCode getPaymentCode() {
        return new PaymentCode(getBip47Account().getPaymentCode());
    }

    public byte[] getFingerprint() {
        return hdWallet.getFingerprint();
    }

    public Collection<CahootsUtxo> getUtxosWpkhByAccount(int account) {
        return utxoProvider.getUtxosWpkhByAccount(account);
    }
}
