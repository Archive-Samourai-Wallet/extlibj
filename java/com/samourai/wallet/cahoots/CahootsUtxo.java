package com.samourai.wallet.cahoots;

import com.samourai.wallet.hd.HD_Address;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.UTXO;
import com.samourai.wallet.send.provider.UtxoKeyProvider;
import com.samourai.wallet.util.UtxoUtil;
import com.samourai.wallet.utxo.BipUtxo;
import com.samourai.wallet.utxo.BipUtxoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.Collectors;

public class CahootsUtxo extends BipUtxoImpl implements BipUtxo {
    private static final Logger log = LoggerFactory.getLogger(CahootsUtxo.class);
    private static final UtxoUtil utxoUtil = UtxoUtil.getInstance();

    private byte[] key;

    public CahootsUtxo(BipUtxo bipUtxo, byte[] key) {
        super(bipUtxo);
        this.key = key;
    }

    public CahootsUtxo(MyTransactionOutPoint outPoint, Integer confirmedBlockHeight, String walletXpub, HD_Address hdAddress) {
        super(new BipUtxoImpl(outPoint, confirmedBlockHeight, walletXpub, hdAddress));
        this.key = hdAddress.getECKey().getPrivKeyBytes();
    }

    public CahootsUtxo(CahootsUtxo cahootsUtxo) {
        super(cahootsUtxo);
        this.key = cahootsUtxo.getKey();
    }

    public byte[] getKey() {
        return key;
    }

    public static long sumValue(Collection<CahootsUtxo> utxos) {
        return utxos.stream().mapToLong(utxo -> utxo.getValue()).sum();
    }

    public MyTransactionOutPoint getOutpoint() {
        return utxoUtil.computeOutpoint(this);
    }

    public static Collection<CahootsUtxo> toCahootsUtxos(Collection<UTXO> utxos, UtxoKeyProvider keyProvider) {
        return utxos.stream().map(utxo -> {
            try {
                // TODO currently we only only consider first outpoint
                BipUtxo bipUtxo = utxo.toBipUtxos().iterator().next();
                byte[] key = keyProvider._getPrivKey(bipUtxo);
                if (key == null) {
                    throw new Exception("Key not found for utxo: "+bipUtxo);
                }
                return new CahootsUtxo(bipUtxo, key);
            } catch (Exception e) {
                log.warn("Skipping CahootsUtxo: "+utxo+": "+e.getMessage());
                return null;
            }
        }).filter(utxo -> utxo != null).collect(Collectors.toList());
    }
}
