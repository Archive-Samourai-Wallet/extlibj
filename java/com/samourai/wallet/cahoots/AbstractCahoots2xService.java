package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.TypeInteraction;
import com.samourai.wallet.SamouraiWalletConst;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.cahoots.psbt.PSBT;
import com.samourai.wallet.hd.BipAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractCahoots2xService<T extends Cahoots2x, C extends CahootsContext> extends AbstractCahootsService<T,C> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCahoots2xService.class);
    private static final long LOCK_TIME_LENIENCE = 2; // 2 blocks
    public AbstractCahoots2xService(CahootsType cahootsType, BipFormatSupplier bipFormatSupplier, NetworkParameters params) {
        super(cahootsType, bipFormatSupplier, params, TypeInteraction.TX_BROADCAST);
    }

    @Override
    public void verifyResponse(C cahootsContext, T cahoots, T request) throws Exception {
        super.verifyResponse(cahootsContext, cahoots, request);

        if (request != null) {
            // properties should never change once set
            if (cahoots.ts != request.ts) {
                throw new Exception("Invalid altered Cahoots ts");
            }
            if (cahoots.strID != request.strID) {
                throw new Exception("Invalid altered Cahoots strID");
            }
            if (!cahoots.strDestination.equals(request.strDestination)) {
                throw new Exception("Invalid altered Cahoots strDestination");
            }
            if (!Objects.equals(cahoots.strPayNymCollab, request.strPayNymCollab)) {
                throw new Exception("Invalid altered Cahoots strPayNymCollab");
            }
            if (!Objects.equals(cahoots.strPayNymInit, request.strPayNymInit)) {
                throw new Exception("Invalid altered Cahoots strPayNymInit");
            }
            if (cahoots.account != request.account) {
                throw new Exception("Invalid altered Cahoots account");
            }
            if (cahoots.cptyAccount != request.cptyAccount) {
                throw new Exception("Invalid altered Cahoots cptyAccount");
            }
            if (!Arrays.equals(cahoots.fingerprint, request.fingerprint)) {
                throw new Exception("Invalid altered Cahoots fingerprint");
            }
            if (!Arrays.equals(cahoots.fingerprintCollab, request.fingerprintCollab)) {
                throw new Exception("Invalid altered Cahoots fingerprintCollab");
            }
        }

        if (cahoots.getStep() >= 3) {
            // check fee
            if (cahoots.feeAmount > SamouraiWalletConst.MAX_ACCEPTABLE_FEES) {
                throw new Exception("Cahoots fee too high: " + cahoots.getTransaction().getFee().longValue());
            }
        }
    }

    protected void checkMaxSpendAmount(T cahoots2x, C cahootsContext) throws Exception {
        long verifiedSpendAmount = computeSpendAmount(cahoots2x.getTransaction(), cahootsContext);
        long maxSpendAmount = computeMaxSpendAmount(cahoots2x, cahootsContext);
        super.checkMaxSpendAmount(cahootsContext, verifiedSpendAmount, maxSpendAmount);
    }

    protected abstract long computeMaxSpendAmount(T cahoots, C cahootsContext) throws Exception;

    protected Transaction signTx(T cahoots, C cahootsContext) throws Exception {
        PSBT psbt = cahoots.getPSBT();
        Transaction transaction = psbt.getTransaction();
        return signTx(cahootsContext, transaction);
    }

    //
    // receiver
    //
    public T doStep3(T cahoots2, C cahootsContext) throws Exception {
        debug("BEGIN doStep3", cahoots2, cahootsContext);

        T cahoots3 = (T)cahoots2.copy();
        checkLockTime(cahoots3, cahootsContext);
        Transaction transaction = signTx(cahoots3, cahootsContext);
        cahoots3.doStep3(transaction);

        // check verifiedSpendAmount
        checkMaxSpendAmount(cahoots3, cahootsContext);

        debug("END doStep3", cahoots3, cahootsContext);
        return cahoots3;
    }

    //
    // sender
    //
    public T doStep4(T cahoots3, C cahootsContext) throws Exception {
        debug("BEGIN doStep4", cahoots3, cahootsContext);

        T cahoots4 = (T)cahoots3.copy();
        checkLockTime(cahoots4, cahootsContext);
        Transaction transaction = signTx(cahoots3, cahootsContext);
        cahoots4.doStep4(transaction);

        // check verifiedSpendAmount
        checkMaxSpendAmount(cahoots4, cahootsContext);

        // check fee
        checkFee(cahoots4);

        debug("END doStep4", cahoots4, cahootsContext);
        return cahoots4;
    }

    // verify

    protected  TransactionOutput computeTxOutput(BipAddress bipAddress, long amount, C cahootsContext) throws Exception{
        return computeTxOutput(bipAddress.getAddressString(), amount, cahootsContext);
    }

    protected  TransactionOutput computeTxOutput(String receiveAddressString, long amount, C cahootsContext) throws Exception{
        cahootsContext.addOutputAddress(receiveAddressString); // save output address for computeSpendAmount()
        return getBipFormatSupplier().getTransactionOutput(receiveAddressString, amount, params);
    }

    protected Collection<TransactionInput> computeSpendInputs(Collection<CahootsUtxo> cahootsUtxos) {
        return cahootsUtxos.stream().map(cahootsUtxo -> utxoUtil.computeSpendInput(cahootsUtxo, params))
                .collect(Collectors.toList());

    }

    private long computeFeeAmountActual(Cahoots2x cahoots) {
        Transaction tx = cahoots.getTransaction();
        long fee = 0;
        for (TransactionInput txInput : tx.getInputs()) {
            long value = cahoots.getOutpointValue(txInput.getOutpoint());
            fee += value;
        }
        for (TransactionOutput txOut : tx.getOutputs()) {
            fee -= txOut.getValue().getValue();
        }
        return fee;
    }

    protected void checkFee(Cahoots2x cahoots) throws Exception {
        long feeActual = computeFeeAmountActual(cahoots);
        long feeExpected = cahoots.getFeeAmount();
        if (log.isDebugEnabled()) {
            log.debug("checkFee: feeActual="+feeActual+", feeExpected="+feeExpected);
        }
        int PRECISION = 2;
        if (Math.abs(feeActual - feeExpected) > PRECISION) {
            throw new Exception("Invalid Cahoots fee: actual="+feeActual+", expected="+feeExpected);
        }
    }

    protected void checkLockTime(T cahoots, CahootsContext cahootsContext) throws Exception {
        long txLockTime = cahoots.getTransaction().getLockTime();
        long currentBlockHeight = cahootsContext.getCahootsWallet().getChainSupplier().getLatestBlock().height;
        if(cahootsContext.getCahootsType() != CahootsType.STOWAWAY) {
            if (txLockTime == 0 || Math.abs(txLockTime - currentBlockHeight) > LOCK_TIME_LENIENCE) { // maybe a block is found fast and users dont have exact same block heights, or the user is running custom code and is malicious
                throw new Exception("Locktime error: txLockTime " + txLockTime + ", vs currentBlockHeight " + currentBlockHeight);
            }
        }
    }

    public void debug(String info, T cahoots, C cahootsContext) {
        if (log.isDebugEnabled()) {
            log.debug("###### " +info+ " "+cahootsContext.getCahootsType()+"/"+cahootsContext.getTypeUser());
            log.debug(" * outpoints="+cahoots.outpoints);
            log.debug(" * tx="+cahoots.getTransaction());
        }
    }

    @Override
    protected CahootsResult computeCahootsResult(C cahootsContext, T cahoots) {
        return new Cahoots2xResult(cahootsContext, cahoots);
    }
}
