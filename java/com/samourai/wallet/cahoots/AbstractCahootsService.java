package com.samourai.wallet.cahoots;

import com.samourai.wallet.send.MyTransactionOutPoint;
import org.bitcoinj.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractCahootsService<T extends Cahoots> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCahootsService.class);

    protected NetworkParameters params;

    public AbstractCahootsService(NetworkParameters params) {
        this.params = params;
    }

    public abstract T startCollaborator(CahootsWallet cahootsWallet, int account, T payload0) throws Exception;

    public abstract T reply(CahootsWallet cahootsWallet, T payload) throws Exception;

    protected HashMap<String, ECKey> computeKeyBag(Cahoots cahoots, List<CahootsUtxo> utxos) {
        // utxos by hash
        HashMap<String, CahootsUtxo> utxosByHash = new HashMap<String, CahootsUtxo>();
        for (CahootsUtxo utxo : utxos) {
            MyTransactionOutPoint outpoint = utxo.getOutpoint();
            utxosByHash.put(outpoint.getTxHash().toString() + "-" + outpoint.getTxOutputN(), utxo);
        }

        Transaction transaction = cahoots.getTransaction();
        HashMap<String, ECKey> keyBag = new HashMap<String, ECKey>();
        for (TransactionInput input : transaction.getInputs()) {
            TransactionOutPoint outpoint = input.getOutpoint();
            String key = outpoint.getHash().toString() + "-" + outpoint.getIndex();
            if (utxosByHash.containsKey(key)) {
                CahootsUtxo utxo = utxosByHash.get(key);
                ECKey eckey = utxo.getKey();
                keyBag.put(outpoint.toString(), eckey);
            }
        }
        return keyBag;
    }
}
