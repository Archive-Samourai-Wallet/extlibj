package com.samourai.wallet.send.spend;

import com.samourai.wallet.SamouraiWalletConst;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.send.beans.SpendError;
import com.samourai.wallet.send.beans.SpendTx;
import com.samourai.wallet.send.beans.SpendType;
import com.samourai.wallet.send.exceptions.SpendException;
import com.samourai.wallet.send.provider.UtxoProvider;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public abstract class SpendSelection {
    private static final Logger log = LoggerFactory.getLogger(SpendSelection.class);
    private BipFormatSupplier bipFormatSupplier;
    private SpendType spendType;
    private List<UTXO> selectedUTXO;

    public SpendSelection(BipFormatSupplier bipFormatSupplier, SpendType spendType) {
        this.bipFormatSupplier = bipFormatSupplier;
        this.spendType = spendType;
        this.selectedUTXO = new ArrayList<>();
    }

    protected BipFormatSupplier getBipFormatSupplier() {
        return bipFormatSupplier;
    }

    public SpendType getSpendType() {
        return spendType;
    }

    public void addSelectedUTXO(UTXO utxo) {
        selectedUTXO.add(utxo);
    }

    public List<MyTransactionOutPoint> getSpendFrom() {
        final List<MyTransactionOutPoint> outPoints = new ArrayList<MyTransactionOutPoint>();
        for (UTXO u : selectedUTXO) {
            outPoints.addAll(u.getOutpoints());
        }
        return outPoints;
    }

    public long getTotalValueSelected() {
        return UTXO.sumValue(selectedUTXO);
    }

    public abstract SpendTx spendTx(long amount, String address, BipFormat changeFormat, WhirlpoolAccount account, boolean rbfOptIn, NetworkParameters params, BigInteger feePerKb, UtxoProvider utxoProvider, long blockHeight) throws SpendException ;

    protected long computeChange(long amount, BigInteger fee) throws SpendException {
        long change = getTotalValueSelected() - (amount + fee.longValue());
        if (change > 0L && change < SamouraiWalletConst.bDust.longValue()) {
            log.warn("SpendError.DUST_CHANGE: change="+change);
            throw new SpendException(SpendError.DUST_CHANGE);
        }
        return change;
    }
}
