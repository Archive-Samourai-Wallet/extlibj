package com.samourai.wallet.cahoots.stonewallx2;

import com.samourai.wallet.cahoots.Cahoots2x;
import com.samourai.wallet.cahoots.CahootsType;
import org.bitcoinj.core.NetworkParameters;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class STONEWALLx2 extends Cahoots2x {
    private static final Logger log = LoggerFactory.getLogger(STONEWALLx2.class);

    private STONEWALLx2()    { ; }

    private STONEWALLx2(STONEWALLx2 c)    {
        super(c);
    }

    public STONEWALLx2(JSONObject obj)    {
        this.fromJSON(obj);
    }

    public STONEWALLx2(long spendAmount, String address, NetworkParameters params, int account, byte[] fingerprint)    {
        super(CahootsType.STONEWALLX2.getValue(), params, spendAmount, address, account, fingerprint);
    }

    @Override
    public STONEWALLx2 copy() {
        return new STONEWALLx2(this);
    }
}