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

public class BipUtxoImpl extends UtxoDetailImpl implements BipUtxo {
    private String walletXpub;
    private boolean bip47;
    private Integer chainIndex;
    private Integer addressIndex;
    private byte[] scriptBytes;

    public BipUtxoImpl(String txHash, int txOutputIndex, long value, String address, Integer confirmedBlockHeight, NetworkParameters params,
                       String walletXpub, boolean bip47, Integer chainIndex, Integer addressIndex, byte[] scriptBytes) {
        super(txHash, txOutputIndex, value, address, confirmedBlockHeight, params);
        this.walletXpub = walletXpub;
        this.bip47 = bip47;
        this.chainIndex = chainIndex;
        this.addressIndex = addressIndex;
        this.scriptBytes = scriptBytes;
    }

    public BipUtxoImpl(TransactionOutput txOut, String address, Integer confirmedBlockHeight,
                       String walletXpub, boolean bip47, Integer chainIndex, Integer addressIndex) {
        this(txOut.getParentTransactionHash().toString(),
                txOut.getIndex(),
                txOut.getValue().getValue(),
                address,
                confirmedBlockHeight,
                txOut.getParams(),
                walletXpub,
                bip47,
                chainIndex,
                addressIndex,
                txOut.getScriptBytes());
    }

    public BipUtxoImpl(MyTransactionOutPoint outPoint, Integer confirmedBlockHeight,
                       String walletXpub, boolean bip47, Integer chainIndex, Integer addressIndex) {
        this(outPoint.getHash().toString(), (int)outPoint.getIndex(), outPoint.getValue().getValue(), outPoint.getAddress(), confirmedBlockHeight, outPoint.getParams(),
                walletXpub, bip47, chainIndex, addressIndex, outPoint.getScriptBytes());
    }

    public BipUtxoImpl(MyTransactionOutPoint outPoint, Integer confirmedBlockHeight,
                       String walletXpub, HD_Address hdAddress) {
        this(outPoint.getHash().toString(), (int)outPoint.getIndex(), outPoint.getValue().getValue(), outPoint.getAddress(), confirmedBlockHeight, outPoint.getParams(),
                walletXpub, false, hdAddress.getChainIndex(), hdAddress.getAddressIndex(), outPoint.getScriptBytes());
    }

    public BipUtxoImpl(BipUtxo bipUtxo) {
        super(bipUtxo);
        this.walletXpub = bipUtxo.getWalletXpub();
        this.bip47 = bipUtxo.isBip47();
        this.chainIndex = bipUtxo.getChainIndex();
        this.addressIndex = bipUtxo.getAddressIndex();
        this.scriptBytes = bipUtxo.getScriptBytes();
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
    public byte[] getScriptBytes() {
        return scriptBytes;
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
