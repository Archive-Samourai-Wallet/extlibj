package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.ManualCahootsMessage;
import com.samourai.soroban.cahoots.TxBroadcastInteraction;
import com.samourai.soroban.cahoots.TypeInteraction;
import com.samourai.soroban.client.SorobanInteraction;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.KeyBag;
import com.samourai.wallet.send.SendFactoryGeneric;
import com.samourai.wallet.util.TxUtil;
import com.samourai.wallet.util.UtxoUtil;
import com.samourai.wallet.utxo.InputOutPoint;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public abstract class AbstractCahootsService<T extends Cahoots, C extends CahootsContext> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCahootsService.class);
    protected static final UtxoUtil utxoUtil = UtxoUtil.getInstance();
    protected static final TxUtil txUtil = TxUtil.getInstance();
    protected static final SendFactoryGeneric sendFactory = SendFactoryGeneric.getInstance();

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

    public SorobanInteraction checkInteraction(ManualCahootsMessage request, T cahootsResponse, C cahootsContext) {
        // broadcast by SENDER
        if (request.getTypeUser().getPartner().equals(typeInteractionBroadcast.getTypeUser()) && (request.getStep()+1) == typeInteractionBroadcast.getStep()) {
            CahootsResult cahootsResult = computeCahootsResult(cahootsContext, cahootsResponse);
            return new TxBroadcastInteraction(typeInteractionBroadcast, new ManualCahootsMessage(cahootsResponse), cahootsContext, cahootsResult);
        }
        return null;
    }

    protected Transaction signTx(C cahootsContext, Transaction transaction) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("signTx:" + transaction.toString());
        }
        // set input info from cahoots context
        Function<TransactionOutPoint, InputOutPoint> getInputOutPoint = i -> cahootsContext.findInput(i);
        return sendFactory.signTransaction(transaction, cahootsContext.getKeyBag(), BIP_FORMAT.PROVIDER, getInputOutPoint);
    }

    protected void checkMaxSpendAmount(C cahootsContext, long verifiedSpendAmount, long maxSpendAmount) throws Exception {
        String prefix = "["+cahootsContext.getCahootsType()+"/"+cahootsContext.getTypeUser()+"] ";
        if (log.isDebugEnabled()) {
            log.debug(prefix+cahootsContext.getTypeUser()+" verifiedSpendAmount="+verifiedSpendAmount+", maxSpendAmount="+maxSpendAmount);
        }
        if (verifiedSpendAmount == 0) {
            throw new Exception(prefix+"Cahoots spendAmount verification failed");
        }
        if (verifiedSpendAmount > maxSpendAmount) {
            throw new Exception(prefix+"Cahoots verifiedSpendAmount mismatch: " + verifiedSpendAmount+" > "+maxSpendAmount);
        }
    }

    protected long computeSpendAmount(Transaction tx, C cahootsContext) throws Exception {
        KeyBag keyBag = cahootsContext.getKeyBag();
        String prefix = "["+cahootsContext.getCahootsType()+"/"+cahootsContext.getTypeUser()+"] ";
        if (log.isDebugEnabled()) {
            log.debug(prefix+"computeSpendAmount: keyBag="+keyBag);
        }
        Function<TransactionOutPoint, InputOutPoint> getInputOutPoint = i -> cahootsContext.findInput(i);
        return txUtil.computeSpendAmount(tx, keyBag, cahootsContext.getOutputAddresses(), getBipFormatSupplier(), getInputOutPoint);
    }

    protected abstract CahootsResult computeCahootsResult(C cahootsContext, T cahoots);

    public BipFormatSupplier getBipFormatSupplier() {
        return bipFormatSupplier;
    }
}
