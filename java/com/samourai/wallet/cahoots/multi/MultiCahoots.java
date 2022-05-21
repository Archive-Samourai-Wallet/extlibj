package com.samourai.wallet.cahoots.multi;

import com.samourai.wallet.cahoots.Cahoots;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots._TransactionOutput;
import com.samourai.wallet.cahoots.stonewallx2.STONEWALLx2;
import com.samourai.wallet.cahoots.stowaway.Stowaway;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.util.RandomUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

public class MultiCahoots extends Cahoots {
    private static final Logger log = LoggerFactory.getLogger(MultiCahoots.class);

    private Stowaway stowaway;
    private STONEWALLx2 stonewallx2;

    private MultiCahoots()    { ; }

    public MultiCahoots(MultiCahoots multiCahoots)    {
        super(multiCahoots);
        this.stonewallx2 = new STONEWALLx2(multiCahoots.stonewallx2);
        this.stowaway = new Stowaway(multiCahoots.stowaway);
    }

    public MultiCahoots(JSONObject obj)    {
        this.fromJSON(obj);
    }

    // Stowaway
    public MultiCahoots(long spendAmount, NetworkParameters params, int account, Stowaway stowaway, STONEWALLx2 stonewallx2)    {
        this.ts = System.currentTimeMillis() / 1000L;
        SecureRandom random = RandomUtil.getSecureRandom();
        this.strID = Hex.toHexString(Sha256Hash.hash(BigInteger.valueOf(random.nextLong()).toByteArray()));
        this.type = CahootsType.MULTI.getValue();
        this.step = 0;
        this.spendAmount = spendAmount;
        this.outpoints = new HashMap<String, Long>();
        this.params = params;
        this.account = account;

        this.stowaway = stowaway;
        this.stonewallx2 = stonewallx2;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = super.toJSON();
        jsonObject.put("stonewallx2", stonewallx2.toJSON());
        jsonObject.put("stowaway", stowaway.toJSON());
        return jsonObject;
    }

    @Override
    public void fromJSON(JSONObject cObj) {
        super.fromJSON(cObj);
        stonewallx2 = new STONEWALLx2(cObj.getJSONObject("stonewallx2"));
        stowaway = new Stowaway(cObj.getJSONObject("stowaway"));
    }

    @Override
    public void signTx(HashMap<String,ECKey> keyBag) {
        if(getStep() > 3) {
            stonewallx2.signTx(keyBag);
        } else {
            stowaway.signTx(keyBag);
        }
    }

    public Stowaway getStowaway() {
        return stowaway;
    }

    public void setStowaway(Stowaway stowaway) {
        this.stowaway = stowaway;
    }

    public STONEWALLx2 getStonewallx2() {
        return stonewallx2;
    }

    public void setStonewallx2(STONEWALLx2 stonewallx2) {
        this.stonewallx2 = stonewallx2;
    }
}
