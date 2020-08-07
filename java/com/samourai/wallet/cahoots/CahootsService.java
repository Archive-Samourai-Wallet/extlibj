package com.samourai.wallet.cahoots;

import org.bitcoinj.core.NetworkParameters;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CahootsService {
    private static final Logger log = LoggerFactory.getLogger(CahootsService.class);

    private CahootsWallet cahootsWallet;
    private STONEWALLx2Service stonewallx2Service;

    public CahootsService(NetworkParameters params, CahootsWallet cahootsWallet) {
        this.cahootsWallet = cahootsWallet;
        this.stonewallx2Service = new STONEWALLx2Service(params);
    }

    public Cahoots start(int type, long amount, String address) throws Exception {
        switch (type) {
            case Cahoots.CAHOOTS_STOWAWAY:
                return null;// TODO ZL CahootsUtil.getInstance(ManualCahootsActivity.this).doStowaway0(amount, account);
            case Cahoots.CAHOOTS_STONEWALLx2:
                byte[] fingerprint = cahootsWallet.getHdWallet().getFingerprint();
                int account = cahootsWallet.getAccount();
                return stonewallx2Service.doSTONEWALLx2_0(amount, address, account, fingerprint);
            default:
                throw new Exception("Unrecognized #Cahoots");
        }
    }

    public Cahoots resume(String cahootsPayload) throws Exception {
        if (!Cahoots.isCahoots(cahootsPayload.trim())) {
            throw new Exception("Unrecognized #Cahoots");
        }
        JSONObject obj = new JSONObject(cahootsPayload);

        if (!obj.has("cahoots") || !obj.getJSONObject("cahoots").has("type")) {
            throw new Exception("Invalid #Cahoots");
        }
        int type = obj.getJSONObject("cahoots").getInt("type");
        switch(type) {
            case Cahoots.CAHOOTS_STOWAWAY:
                Stowaway stowaway = new Stowaway(obj);
                // TODO ZL
                return stowaway;
            case Cahoots.CAHOOTS_STONEWALLx2:
                STONEWALLx2 stonewallx2 = new STONEWALLx2(obj);
                return stonewallx2Service.process(stonewallx2, cahootsWallet);
            default:
                throw new Exception("Unrecognized #Cahoots");
        }
    }
}
