package com.samourai.wallet.utxo;

import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.bipWallet.WalletSupplier;
import com.samourai.wallet.hd.BipAddress;
import org.bitcoinj.core.NetworkParameters;

public class BipUtxoImpl extends UtxoDetailImpl implements BipUtxo {
    private String xpub;
    private boolean bip47;
    private Integer chainIndex;
    private Integer addressIndex;
    private byte[] scriptBytes;

    public BipUtxoImpl(String txHash, int txOutputIndex, long value, String address, Integer confirmedBlockHeight,
                       String xpub, boolean bip47, Integer chainIndex, Integer addressIndex, byte[] scriptBytes) {
        super(txHash, txOutputIndex, value, address, confirmedBlockHeight);
        this.xpub = xpub;
        this.bip47 = bip47;
        this.chainIndex = chainIndex;
        this.addressIndex = addressIndex;
        this.scriptBytes = scriptBytes;
    }

    @Override
    public BipWallet getBipWallet(WalletSupplier walletSupplier) {
        return walletSupplier.getWalletByXPub(xpub);
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
    public byte[] getScriptBytes() {
        return scriptBytes;
    }

    @Override
    public String toString() {
        return super.toString()+
                ", xpub='" + xpub + '\'' +
                ", bip47=" + bip47 +
                ", chainIndex=" + chainIndex +
                ", addressIndex=" + addressIndex;
    }
}
