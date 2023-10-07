package com.samourai.wallet.api.backend.beans;

import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.bipWallet.WalletSupplier;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.util.UtxoUtil;
import com.samourai.wallet.utxo.BipUtxo;
import com.samourai.wallet.utxo.UtxoConfirmInfo;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bouncycastle.util.encoders.Hex;

import java.util.Collection;

public class UnspentOutput implements BipUtxo {
    private static final UtxoUtil utxoUtil = UtxoUtil.getInstance();
    public String tx_hash;
    public int tx_output_n;
    public int tx_version;
    public long tx_locktime;
    public long value;
    public String script;
    public String addr;
    public int confirmations;
    public Xpub xpub;
    private UtxoConfirmInfo confirmInfo; // used for BipUtxo

    public UnspentOutput() {
    }

    public UnspentOutput(UnspentOutput copy) {
        this.tx_hash = copy.tx_hash;
        this.tx_output_n = copy.tx_output_n;
        this.tx_version = copy.tx_version;
        this.tx_locktime = copy.tx_locktime;
        this.value = copy.value;
        this.script = copy.script;
        this.addr = copy.addr;
        this.confirmations = copy.confirmations;
        this.xpub = copy.xpub;
    }

    public UnspentOutput(MyTransactionOutPoint outPoint, String path, String xpub) {
        this.tx_hash = outPoint.getTxHash();
        this.tx_output_n = outPoint.getTxOutputIndex();
        this.tx_version = -1; // ignored
        this.tx_locktime = -1; // ignored
        this.value = outPoint.getValue().getValue();
        this.script = outPoint.getScriptBytes() != null ? Hex.toHexString(outPoint.getScriptBytes()) : null;
        this.addr = outPoint.getAddress();
        this.confirmations = outPoint.getConfirmations();
        this.xpub = new Xpub();
        this.xpub.path = path;
        this.xpub.m = xpub;
    }

    public String getPath() {
        if (xpub == null) {
            return null;
        }
        return xpub.path;
    }

    private String getXpubM() {
        return xpub != null ? xpub.m : null;
    }

    public static long sumValue(Collection<UnspentOutput> utxos) {
        return utxos.stream().mapToLong(utxo -> utxo.getValueLong()).sum();
    }

    public static class Xpub {
      public String m;
      public String path;
    }

    @Override
    public String toString() {
      return tx_hash
          + ":"
          + tx_output_n
          + " ("
          + value
          + " sats, "
          + confirmations
          + " confirmations"
          + ", path=" + (xpub != null && xpub.path != null ? xpub.path : "null")
          + ", xpub=" + (xpub != null && xpub.m != null ? xpub.m : "null")
          + ", address="
          + addr
          + ")";
    }

    // implement BipUtxo

    @Override
    public String getTxHash() {
        return tx_hash;
    }

    @Override
    public int getTxOutputIndex() {
        return tx_output_n;
    }

    @Override
    public long getValueLong() {
        return value;
    }

    @Override
    public String getAddress() {
        return addr;
    }

    @Override
    public UtxoConfirmInfo getConfirmInfo() {
        if (confirmInfo == null) {
            confirmInfo = new UtxoConfirmInfo() {
                @Override
                public Integer getConfirmedBlockHeight() {
                    return null; // unknown
                }

                @Override
                public boolean isConfirmed() {
                    return confirmations > 0;
                }

                @Override
                public int getConfirmations(int latestBlockHeight) {
                    return confirmations;
                }
            };
        }
        return confirmInfo;
    }

    @Override
    public void setConfirmInfo(UtxoConfirmInfo confirmInfo) {
        this.confirmInfo = confirmInfo;
    }

    @Override
    public BipWallet getBipWallet(WalletSupplier walletSupplier) {
        if (isBip47()) {
            return null; // bip47
        }
        return walletSupplier.getWalletByXPub(getXpubM());
    }

    @Override
    public BipAddress getBipAddress(WalletSupplier walletSupplier) {
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
        return xpub == null || StringUtils.isEmpty(xpub.path);
    }

    @Override
    public Integer getChainIndex() {
        return getPath() != null ? utxoUtil.computeChainIndex(getPath()) : null;
    }

    @Override
    public Integer getAddressIndex() {
        return getPath() != null ? utxoUtil.computeAddressIndex(getPath()) :  null;
    }

    @Override
    public byte[] getScriptBytes() {
        return script != null ? Hex.decode(script) : null;
    }

    @Override
    public String getWalletXpub() {
        return getXpubM();
    }
}