package com.samourai.wallet.send;

import com.samourai.wallet.hd.AddressType;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.TransactionOutPoint;

import java.util.Collection;

public interface UtxoProvider {

    String getChangeAddress(WhirlpoolAccount account, AddressType addressType);

    Collection<UTXO> getUtxos(WhirlpoolAccount account);

    Collection<UTXO> getUtxos(WhirlpoolAccount account, AddressType addressType);

    ECKey _getPrivKey(TransactionOutPoint outPoint, WhirlpoolAccount account) throws Exception;
}
