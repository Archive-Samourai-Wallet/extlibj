package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.TypeInteraction;
import com.samourai.wallet.SamouraiWalletConst;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class AbstractCahoots2xService<T extends Cahoots2x> extends AbstractCahootsService<T,CahootsContext> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCahoots2xService.class);

    public AbstractCahoots2xService(CahootsType cahootsType, BipFormatSupplier bipFormatSupplier, NetworkParameters params) {
        super(cahootsType, bipFormatSupplier, params, TypeInteraction.TX_BROADCAST);
    }

    @Override
    public void verifyResponse(CahootsContext cahootsContext, T cahoots, T request) throws Exception {
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
            if (!Objects.equals(cahoots.strCollabChange, request.strCollabChange)) {
                throw new Exception("Invalid altered Cahoots strCollabChange");
            }
        }

        if (cahoots.getStep() >= 3) {
            // check fee
            if (cahoots.feeAmount > SamouraiWalletConst.MAX_ACCEPTABLE_FEES) {
                throw new Exception("Cahoots fee too high: " + cahoots.getTransaction().getFee().longValue());
            }
        }
    }

    protected void checkMaxSpendAmount(long verifiedSpendAmount, long feeAmount, CahootsContext cahootsContext) throws Exception {
        long maxSpendAmount = computeMaxSpendAmount(feeAmount, cahootsContext);
        if (log.isDebugEnabled()) {
            log.debug(cahootsContext.getTypeUser()+" verifiedSpendAmount="+verifiedSpendAmount+", maxSpendAmount="+maxSpendAmount);
        }
        if (verifiedSpendAmount == 0) {
            throw new Exception("Cahoots spendAmount verification failed");
        }
        if (verifiedSpendAmount > maxSpendAmount) {
            throw new Exception("Cahoots verifiedSpendAmount mismatch: " + verifiedSpendAmount);
        }
    }

    protected abstract long computeMaxSpendAmount(long minerFee, CahootsContext cahootsContext) throws Exception;

    //
    // receiver
    //
    public T doStep3(T cahoots2, CahootsContext cahootsContext) throws Exception {
        CahootsWallet cahootsWallet = cahootsContext.getCahootsWallet();
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(cahoots2.getCounterpartyAccount());
        HashMap<String, ECKey> keyBag_A = computeKeyBag(cahoots2, utxos);

        T cahoots3 = (T)cahoots2.copy();
        cahoots3.doStep3(keyBag_A);

        // check verifiedSpendAmount
        long verifiedSpendAmount = computeSpendAmount(keyBag_A, cahoots3, cahootsContext);
        checkMaxSpendAmount(verifiedSpendAmount, cahoots3.getFeeAmount(), cahootsContext);
        return cahoots3;
    }

    //
    // sender
    //
    public T doStep4(T cahoots3, CahootsContext cahootsContext) throws Exception {
        CahootsWallet cahootsWallet = cahootsContext.getCahootsWallet();
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(cahoots3.getAccount());
        HashMap<String, ECKey> keyBag_B = computeKeyBag(cahoots3, utxos);

        T cahoots4 = (T)cahoots3.copy();
        cahoots4.doStep4(keyBag_B);

        // check verifiedSpendAmount
        long verifiedSpendAmount = computeSpendAmount(keyBag_B, cahoots4, cahootsContext);
        checkMaxSpendAmount(verifiedSpendAmount, cahoots4.getFeeAmount(), cahootsContext);
        return cahoots4;
    }
}
