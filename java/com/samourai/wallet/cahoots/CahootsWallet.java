package com.samourai.wallet.cahoots;

import com.samourai.wallet.segwit.BIP84Wallet;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.whirlpool.WhirlpoolConst;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;

import java.util.HashMap;
import java.util.List;

public abstract class CahootsWallet {
    public abstract ECKey getPrivKey(String address, int account);
    public abstract String getUnspentPath(String address);
    public abstract List<UTXO> getCahootsUTXO(int account);
    public abstract int getHighestPostChangeIdx();
    public abstract BIP84Wallet getBip84Wallet();

    public HashMap<String, ECKey> computeKeyBag(Cahoots cahoots, int myAccount) {
        HashMap<String, String> utxo2Address = new HashMap<String, String>();
        List<UTXO> utxos = getCahootsUTXO(myAccount);
        for (UTXO utxo : utxos) {
            for (MyTransactionOutPoint outpoint : utxo.getOutpoints()) {
                utxo2Address.put(outpoint.getTxHash().toString() + "-" + outpoint.getTxOutputN(), outpoint.getAddress());
            }
        }

        Transaction transaction = cahoots.getTransaction();
        HashMap<String, ECKey> keyBag = new HashMap<String, ECKey>();
        for (TransactionInput input : transaction.getInputs()) {
            TransactionOutPoint outpoint = input.getOutpoint();
            String key = outpoint.getHash().toString() + "-" + outpoint.getIndex();
            if (utxo2Address.containsKey(key)) {
                String address = utxo2Address.get(key);
                ECKey eckey = getPrivKey(address, myAccount);
                keyBag.put(outpoint.toString(), eckey);
            }
        }
        return keyBag;
    }
}
