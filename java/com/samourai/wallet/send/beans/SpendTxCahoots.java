package com.samourai.wallet.send.beans;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.wallet.api.backend.IPushTx;
import com.samourai.wallet.cahoots.Cahoots;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.send.exceptions.SpendException;
import com.samourai.wallet.send.provider.UtxoKeyProvider;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class SpendTxCahoots extends SpendTx {
    private static final Logger log = LoggerFactory.getLogger(SpendTxCahoots.class);
    private Cahoots cahoots;
    private CahootsContext cahootsContext;

    public SpendTxCahoots(Cahoots cahoots, CahootsContext cahootsContext, UtxoKeyProvider utxoKeyProvider) throws SpendException {
        super(CahootsType.find(cahoots.getType()).get().getSpendType(),
                cahoots.getSpendAmount(),
                false,
                cahoots.getFeeAmount(),
                cahootsContext.getMinerFeePaid(),
                cahootsContext.getSamouraiFee(),
                findChangeAmount(cahoots, cahootsContext, utxoKeyProvider),
                cahootsContext.getInputs(),
                findReceivers(cahoots, utxoKeyProvider),
                cahoots.getTransaction().getVirtualTransactionSize(),
                cahoots.getTransaction().getWeight(),
                cahoots.getTransaction().getHashAsString());
        this.cahoots = cahoots;
        this.cahootsContext = cahootsContext;
    }

    private static Map<String,Long> findReceivers(Cahoots cahoots, UtxoKeyProvider utxoKeyProvider) {
        return cahoots.getTransaction().getOutputs().stream()
                .map(transactionOutput -> {
                    try {
                        String toAddress = utxoKeyProvider.getBipFormatSupplier().getToAddress(transactionOutput);
                        if (toAddress != null) {
                            return Pair.of(toAddress, transactionOutput.getValue().value);
                        }
                    } catch (Exception e) {}
                    return null;
                }).filter(pair -> pair != null)
                .collect(Collectors.toMap(pair -> pair.getLeft(), pair -> pair.getRight()));
    }

    private static long findChangeAmount(Cahoots cahoots, CahootsContext cahootsContext, UtxoKeyProvider utxoKeyProvider) {
        return cahoots.getTransaction().getOutputs().stream()
                .filter(transactionOutput -> {
                    try {
                        String toAddress = utxoKeyProvider.getBipFormatSupplier().getToAddress(transactionOutput);
                        return toAddress != null && cahootsContext.getOutputAddresses().contains(toAddress);
                    } catch (Exception e) {
                        log.error("", e);
                        return false;
                    }
                })
                .mapToLong(transactionOutput -> transactionOutput.getValue().value).sum();
    }

    public Cahoots getCahoots() {
        return cahoots;
    }

    public CahootsContext getCahootsContext() {
        return cahootsContext;
    }

    public void pushTx(IPushTx pushTx) throws Exception {
        cahoots.pushTx(pushTx);
    }
}
