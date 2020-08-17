package com.samourai.wallet.cahoots;

import org.bitcoinj.core.NetworkParameters;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CahootsDialog {
    private static final Logger log = LoggerFactory.getLogger(CahootsDialog.class);

    private NetworkParameters params;
    private CahootsWallet cahootsWallet;
    private long feePerB;
    private int account;

    public STONEWALLx2 newStonewallx2(long amount, String address) {
        STONEWALLx2Service stonewallx2Service = new STONEWALLx2Service(params);
        STONEWALLx2 payload0 = stonewallx2Service.startInitiator(cahootsWallet, amount, account, address);
        return payload0;
    }

    public Stowaway newStowaway(long amount) {
        StowawayService stowawayService = new StowawayService(params);
        Stowaway payload0 = stowawayService.startInitiator(cahootsWallet, amount, account);
        return payload0;
    }

    public <T extends Cahoots> T newCollaborator(String cahootsPayload) throws Exception {
        Cahoots payload = parsePayload(cahootsPayload);
        AbstractCahootsService cahootsService = newCahootsService(payload.getType());
        T nextPayload = (T)cahootsService.startCollaborator(payload, cahootsWallet, account);
        return nextPayload;
    }

    public CahootsDialog(NetworkParameters params, CahootsWallet cahootsWallet, long feePerB, int account) {
        this.params = params;
        this.cahootsWallet = cahootsWallet;
        this.feePerB = feePerB;
        this.account = account;
    }

    private Cahoots parsePayload(String cahootsPayload) throws Exception {
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
                return new Stowaway(obj);
            case Cahoots.CAHOOTS_STONEWALLx2:
                return new STONEWALLx2(obj);
        }
        throw new Exception("Unrecognized #Cahoots");
    }

    private AbstractCahootsService newCahootsService(int type) throws Exception {
        switch(type) {
            case Cahoots.CAHOOTS_STOWAWAY:
                return new StowawayService(params);
            case Cahoots.CAHOOTS_STONEWALLx2:
                return new STONEWALLx2Service(params);
        }
        throw new Exception("Unrecognized #Cahoots");
    }

    public <T extends Cahoots> T resume(String payloadStr) throws Exception {
        Cahoots payload = parsePayload(payloadStr);
        AbstractCahootsService cahootsService = newCahootsService(payload.getType());
        return (T)cahootsService.resume(payload, cahootsWallet, feePerB);
    }
}
