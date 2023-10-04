package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.ManualCahootsMessage;
import com.samourai.soroban.cahoots.TxBroadcastInteraction;
import com.samourai.soroban.cahoots.TypeInteraction;
import com.samourai.soroban.client.SorobanInteraction;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.KeyBag;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.util.UtxoUtil;
import org.bitcoinj.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCahootsService<T extends Cahoots, C extends CahootsContext> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCahootsService.class);
    private static final UtxoUtil utxoUtil = UtxoUtil.getInstance();

    private CahootsType cahootsType;
    private BipFormatSupplier bipFormatSupplier;
    protected NetworkParameters params;
    private TypeInteraction typeInteractionBroadcast;

    public AbstractCahootsService(CahootsType cahootsType, BipFormatSupplier bipFormatSupplier, NetworkParameters params, TypeInteraction typeInteractionBroadcast) {
        this.cahootsType = cahootsType;
        this.bipFormatSupplier = bipFormatSupplier;
        this.params = params;
        this.typeInteractionBroadcast = typeInteractionBroadcast;
    }

    public abstract T startInitiator(C cahootsContext) throws Exception;

    public abstract T startCollaborator(C cahootsContext, T payload0) throws Exception;

    public abstract T reply(C cahootsContext, T payload) throws Exception;

    public void verifyResponse(C cahootsContext, T response, T request) throws Exception {
        if (!cahootsContext.getCahootsType().equals(cahootsType)) {
            throw new Exception("Invalid cahootsContext.type: "+cahootsContext.getCahootsType()+" vs "+cahootsType);
        }
        if (request != null) {
            // properties should never change
            if (response.getType() != request.getType()) {
                throw new Exception("Invalid altered Cahoots type");
            }
            if (response.getVersion() != request.getVersion()) {
                throw new Exception("Invalid altered Cahoots version");
            }
            if (!response.getParams().equals(request.getParams())) {
                throw new Exception("Invalid altered Cahoots params");
            }

            // step should increment
            if (response.getStep() != request.getStep() + 1) {
                throw new Exception("Invalid response step");
            }
        }
    }

    public SorobanInteraction checkInteraction(ManualCahootsMessage request, Cahoots cahootsResponse) {
        // broadcast by SENDER
        if (request.getTypeUser().getPartner().equals(typeInteractionBroadcast.getTypeUser()) && (request.getStep()+1) == typeInteractionBroadcast.getStep()) {
            return new TxBroadcastInteraction(typeInteractionBroadcast, cahootsResponse);
        }
        return null;
    }

    // verify

    protected long computeSpendAmount(Cahoots2x cahoots, C cahootsContext) throws Exception {
        KeyBag keyBag = cahootsContext.getKeyBag();
        long spendAmount = 0;

        String prefix = "["+cahootsContext.getCahootsType()+"/"+cahootsContext.getTypeUser()+"] ";
        if (log.isDebugEnabled()) {
            log.debug(prefix+"computeSpendAmount: keyBag="+keyBag);
        }
        Transaction transaction = cahoots.getTransaction();
        for(TransactionInput input : transaction.getInputs()) {
            TransactionOutPoint outpoint = input.getOutpoint();

            if (keyBag.getPrivKeyBytes(outpoint) != null) {
                Long inputValue = cahoots.getOutpointValue(outpoint);
                if (inputValue != null) {
                    if (log.isDebugEnabled()) {
                        log.debug(prefix+"computeSpendAmount: +input "+inputValue + " "+outpoint.toString());
                    }
                    spendAmount += inputValue;
                }
            }
        }

        for(TransactionOutput output : transaction.getOutputs()) {
            if (!output.getScriptPubKey().isOpReturn()) {
                String outputAddress = bipFormatSupplier.getToAddress(output);
                if (outputAddress != null && cahootsContext.getOutputAddresses().contains(outputAddress)) {
                    if (output.getValue() != null) {
                        if (log.isDebugEnabled()) {
                            log.debug(prefix + "computeSpendAmount: -output " + output.getValue().longValue() + " " + outputAddress);
                        }
                        spendAmount -= output.getValue().longValue();
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(prefix+"computeSpendAmount = " + spendAmount);
        }
        return spendAmount;
    }

    protected  TransactionOutput computeTxOutput(BipAddress bipAddress, long amount, C cahootsContext) throws Exception{
        return computeTxOutput(bipAddress.getAddressString(), amount, cahootsContext);
    }

    protected  TransactionOutput computeTxOutput(String receiveAddressString, long amount, C cahootsContext) throws Exception{
        cahootsContext.addOutputAddress(receiveAddressString); // save output address for computeSpendAmount()
        return bipFormatSupplier.getTransactionOutput(receiveAddressString, amount, params);
    }

    private long computeFeeAmountActual(Cahoots cahoots) {
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

    protected void checkFee(Cahoots cahoots) throws Exception {
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

    public BipFormatSupplier getBipFormatSupplier() {
        return bipFormatSupplier;
    }
}
