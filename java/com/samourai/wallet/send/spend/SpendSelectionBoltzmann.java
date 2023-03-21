package com.samourai.wallet.send.spend;

import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.client.indexHandler.IIndexHandler;
import com.samourai.wallet.send.BoltzmannUtil;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.send.beans.SpendError;
import com.samourai.wallet.send.beans.SpendTx;
import com.samourai.wallet.send.beans.SpendType;
import com.samourai.wallet.send.exceptions.SpendException;
import com.samourai.wallet.send.provider.UtxoProvider;
import com.samourai.wallet.util.RandomUtil;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.apache.commons.lang3.tuple.Pair;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.TransactionOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

public class SpendSelectionBoltzmann extends SpendSelection {
    private static final Logger log = LoggerFactory.getLogger(SpendSelectionBoltzmann.class);
    private Pair<ArrayList<MyTransactionOutPoint>, ArrayList<TransactionOutput>> pair;

    public SpendSelectionBoltzmann(BipFormatSupplier bipFormatSupplier, Pair<ArrayList<MyTransactionOutPoint>, ArrayList<TransactionOutput>> pair) {
        super(bipFormatSupplier, SpendType.STONEWALL);
        this.pair = pair;
    }

    public static SpendSelectionBoltzmann compute(long neededAmount, UtxoProvider utxoProvider, BipFormat changeFormat, long amount, String address, WhirlpoolAccount account, BipFormat forcedChangeFormat, NetworkParameters params, BigInteger feePerKb, IIndexHandler changeIndexHandler) {
        int initialChangeIndex = changeIndexHandler.get();

        if (log.isDebugEnabled()) {
            log.debug("needed amount:" + neededAmount);
        }

        Collection<UTXO> _utxos1 = null;
        Collection<UTXO> _utxos2 = null;

        Collection<UTXO> utxosP2WPKH = utxoProvider.getUtxos(account, BIP_FORMAT.SEGWIT_NATIVE);
        Collection<UTXO> utxosP2SH_P2WPKH = utxoProvider.getUtxos(account, BIP_FORMAT.SEGWIT_COMPAT);
        Collection<UTXO> utxosP2PKH = utxoProvider.getUtxos(account, BIP_FORMAT.LEGACY);

        long valueP2WPKH = UTXO.sumValue(utxosP2WPKH);
        long valueP2SH_P2WPKH = UTXO.sumValue(utxosP2SH_P2WPKH);
        long valueP2PKH = UTXO.sumValue(utxosP2PKH);

        if (log.isDebugEnabled()) {
            log.debug("value P2WPKH:" + valueP2WPKH);
            log.debug("value P2SH_P2WPKH:" + valueP2SH_P2WPKH);
            log.debug("value P2PKH:" + valueP2PKH);
        }

        boolean selectedP2WPKH = false;
        boolean selectedP2SH_P2WPKH = false;
        boolean selectedP2PKH = false;

        if ((valueP2WPKH > (neededAmount * 2)) && changeFormat == BIP_FORMAT.SEGWIT_NATIVE) {
            if (log.isDebugEnabled()) {
                log.debug("set 1 P2WPKH 2x");
            }
            _utxos1 = utxosP2WPKH;
            selectedP2WPKH = true;
        } else if (changeFormat == BIP_FORMAT.SEGWIT_COMPAT && (valueP2SH_P2WPKH > (neededAmount * 2))) {
            if (log.isDebugEnabled()) {
                log.debug("set 1 P2SH_P2WPKH 2x");
            }
            _utxos1 = utxosP2SH_P2WPKH;
            selectedP2SH_P2WPKH = true;
        } else if (changeFormat == BIP_FORMAT.LEGACY && (valueP2PKH > (neededAmount * 2))) {
            if (log.isDebugEnabled()) {
                log.debug("set 1 P2PKH 2x");
            }
            _utxos1 = utxosP2PKH;
            selectedP2PKH = true;
        } else if (valueP2WPKH > (neededAmount * 2)) {
            if (log.isDebugEnabled()) {
                log.debug("set 1 P2WPKH 2x");
            }
            _utxos1 = utxosP2WPKH;
            selectedP2WPKH = true;
        } else if (valueP2SH_P2WPKH > (neededAmount * 2)) {
            if (log.isDebugEnabled()) {
                log.debug("set 1 P2SH_P2WPKH 2x");
            }
            _utxos1 = utxosP2SH_P2WPKH;
            selectedP2SH_P2WPKH = true;
        } else if (valueP2PKH > (neededAmount * 2)) {
            if (log.isDebugEnabled()) {
                log.debug("set 1 P2PKH 2x");
            }
            _utxos1 = utxosP2PKH;
            selectedP2PKH = true;
        } else {
            ;
        }

        if (_utxos1 == null || _utxos1.size() == 0) {
            if (valueP2SH_P2WPKH > neededAmount) {
                if (log.isDebugEnabled()) {
                    log.debug("set 1 P2SH_P2WPKH");
                }
                _utxos1 = utxosP2SH_P2WPKH;
                selectedP2SH_P2WPKH = true;
            } else if (valueP2WPKH > neededAmount) {
                if (log.isDebugEnabled()) {
                    log.debug("set 1 P2WPKH");
                }
                _utxos1 = utxosP2WPKH;
                selectedP2WPKH = true;
            } else if (valueP2PKH > neededAmount) {
                if (log.isDebugEnabled()) {
                    log.debug("set 1 P2PKH");
                }
                _utxos1 = utxosP2PKH;
                selectedP2PKH = true;
            } else {
                ;
            }
        }

        if (_utxos1 != null && _utxos1.size() > 0) {
            if (!selectedP2SH_P2WPKH && valueP2SH_P2WPKH > neededAmount) {
                if (log.isDebugEnabled()) {
                    log.debug("set 2 P2SH_P2WPKH");
                }
                _utxos2 = utxosP2SH_P2WPKH;
                selectedP2SH_P2WPKH = true;
            }
            if (!selectedP2SH_P2WPKH && !selectedP2WPKH && valueP2WPKH > neededAmount) {
                if (log.isDebugEnabled()) {
                    log.debug("set 2 P2WPKH");
                }
                _utxos2 = utxosP2WPKH;
                selectedP2WPKH = true;
            }
            if (!selectedP2SH_P2WPKH && !selectedP2WPKH && !selectedP2PKH && valueP2PKH > neededAmount) {
                if (log.isDebugEnabled()) {
                    log.debug("set 2 P2PKH");
                }
                _utxos2 = utxosP2PKH;
                selectedP2PKH = true;
            } else {
                ;
            }
        }

        if ((_utxos1 == null || _utxos1.size() == 0) && (_utxos2 == null || _utxos2.size() == 0)) {
            // can't do boltzmann => revert change index
            changeIndexHandler.set(initialChangeIndex, true);
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("boltzmann spend");
        }

        List<UTXO> _utxos1Shuffled = new ArrayList<>(_utxos1);
        RandomUtil.getInstance().shuffle(_utxos1Shuffled);

        List<UTXO> _utxos2Shuffled = null;
        if (_utxos2 != null && _utxos2.size() > 0) {
            _utxos2Shuffled = new ArrayList<>(_utxos2);
            RandomUtil.getInstance().shuffle(_utxos2Shuffled);
        }

        // boltzmann spend (STONEWALL)
        Pair<ArrayList<MyTransactionOutPoint>, ArrayList<TransactionOutput>> pair = BoltzmannUtil.getInstance().boltzmann(_utxos1Shuffled, _utxos2Shuffled, BigInteger.valueOf(amount), address, account, utxoProvider, forcedChangeFormat, params, feePerKb);

        if (pair == null) {
            // can't do boltzmann => revert change index
            changeIndexHandler.set(initialChangeIndex, true);
            return null;
        }

        return new SpendSelectionBoltzmann(utxoProvider.getBipFormatSupplier(), pair);
    }

    @Override
    public SpendTx spendTx(long amount, String address, BipFormat changeFormat, WhirlpoolAccount account, boolean rbfOptIn, NetworkParameters params, BigInteger feePerKb, UtxoProvider utxoProvider, long blockHeight) throws SpendException {
        // select utxos for boltzmann
        long inputAmount = 0L;
        long outputAmount = 0L;

        for (MyTransactionOutPoint outpoint : pair.getLeft()) {
            UTXO u = new UTXO();
            List<MyTransactionOutPoint> outs = new ArrayList<MyTransactionOutPoint>();
            outs.add(outpoint);
            u.setOutpoints(outs);
            addSelectedUTXO(u);
            inputAmount += u.getValue();
        }

        Map<String, Long> receivers = new HashMap<>();
        for (TransactionOutput output : pair.getRight()) {
            try {
                String outputAddress = getBipFormatSupplier().getToAddress(output);
                if (receivers.containsKey(outputAddress)) {
                    // prevent erasing existing receiver
                    log.error("receiver already set");
                    throw new SpendException(SpendError.MAKING);
                }
                receivers.put(outputAddress, output.getValue().longValue());
                outputAmount += output.getValue().longValue();
            } catch (Exception e) {
                throw new SpendException(SpendError.BIP126_OUTPUT);
            }
        }

        BigInteger fee = BigInteger.valueOf(inputAmount - outputAmount);
        long change = computeChange(amount, fee);
        SpendTx spendTx = computeSpendTx(changeFormat, amount, fee.longValue(), change, receivers, rbfOptIn, utxoProvider, params, blockHeight);
        return spendTx;
    }
}
