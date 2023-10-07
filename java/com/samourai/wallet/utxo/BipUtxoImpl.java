package com.samourai.wallet.utxo;

import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.bipWallet.WalletSupplier;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.hd.HD_Address;
import com.samourai.wallet.send.MyTransactionOutPoint;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.TransactionOutput;

public class BipUtxoImpl extends UtxoOutPointImpl implements BipUtxo {
    private String walletXpub;
    private boolean bip47;
    private Integer chainIndex;
    private Integer addressIndex;

    public BipUtxoImpl(String txHash, int txOutputIndex, long value, String address, UtxoConfirmInfo confirmInfo, byte[] scriptBytes,
                       String walletXpub, boolean bip47, Integer chainIndex, Integer addressIndex) {
        super(txHash, txOutputIndex, value, address, confirmInfo, scriptBytes);
        this.walletXpub = walletXpub;
        this.bip47 = bip47;
        this.chainIndex = chainIndex;
        this.addressIndex = addressIndex;
    }

    public BipUtxoImpl(TransactionOutput txOut, String address, UtxoConfirmInfo confirmInfo,
                       String walletXpub, boolean bip47, Integer chainIndex, Integer addressIndex) {
        this(txOut.getParentTransactionHash().toString(),
                txOut.getIndex(),
                txOut.getValue().getValue(),
                address,
                confirmInfo,
                txOut.getScriptBytes(),
                walletXpub,
                bip47,
                chainIndex,
                addressIndex);
    }

    public BipUtxoImpl(BipUtxo bipUtxo) {
        super(bipUtxo);
        this.walletXpub = bipUtxo.getWalletXpub();
        this.bip47 = bipUtxo.isBip47();
        this.chainIndex = bipUtxo.getChainIndex();
        this.addressIndex = bipUtxo.getAddressIndex();
    }

    public BipUtxoImpl(MyTransactionOutPoint outPoint, UtxoConfirmInfo confirmInfo,
                       String walletXpub, boolean bip47, Integer chainIndex, Integer addressIndex) {
        this(outPoint.getHash().toString(), (int)outPoint.getIndex(), outPoint.getValue().getValue(), outPoint.getAddress(), confirmInfo, outPoint.getScriptBytes(),
                walletXpub, bip47, chainIndex, addressIndex);
    }

    public BipUtxoImpl(MyTransactionOutPoint outPoint, UtxoConfirmInfo confirmInfo,
                       String walletXpub, HD_Address hdAddress) {
        this(outPoint.getHash().toString(), (int)outPoint.getIndex(), outPoint.getValue().getValue(), outPoint.getAddress(), confirmInfo, outPoint.getScriptBytes(),
                walletXpub, false, hdAddress.getChainIndex(), hdAddress.getAddressIndex());
    }

    @Override
    public String getWalletXpub() {
        return walletXpub;
    }

    @Override
    public BipWallet getBipWallet(WalletSupplier walletSupplier) {
        return walletSupplier.getWalletByXPub(walletXpub);
    }

    @Override
    public BipAddress getBipAddress(WalletSupplier walletSupplier) {
        if (isBip47()) {
            return null; // bip47
        }
        BipWallet bipWallet = getBipWallet(walletSupplier);
        if (bipWallet == null) {
            return null;
        }
        NetworkParameters params = bipWallet.getParams();
        BipFormat bipFormat = getBipFormat(walletSupplier.getBipFormatSupplier(), params);
        return bipWallet.getAddressAt(getChainIndex(), getAddressIndex(), bipFormat);
    }

    @Override
    public BipFormat getBipFormat(BipFormatSupplier bipFormatSupplier, NetworkParameters params) {
        return bipFormatSupplier.findByAddress(getAddress(), params);
    }

    @Override
    public boolean isBip47() {
        return bip47;
    }

    @Override
    public Integer getChainIndex() {
        return chainIndex;
    }

    @Override
    public Integer getAddressIndex() {
        return addressIndex;
    }

    @Override
    public String toString() {
        return super.toString()+
                ", walletXpub='" + walletXpub + '\'' +
                ", bip47=" + bip47 +
                ", chainIndex=" + chainIndex +
                ", addressIndex=" + addressIndex;
    }
}
