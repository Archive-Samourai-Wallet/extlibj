package com.samourai.wallet.cahoots;

import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CahootsService {
    private static final Logger log = LoggerFactory.getLogger(CahootsService.class);

    private NetworkParameters params;
    private CahootsWallet cahootsWallet;
    private long feePerB;
    private int account;

    public CahootsService(NetworkParameters params, CahootsWallet cahootsWallet, long feePerB, int account) {
        this.params = params;
        this.cahootsWallet = cahootsWallet;
        this.feePerB = feePerB;
        this.account = account;
    }

    public CahootsMessage newStonewallx2(long amount, String address) {
        STONEWALLx2Service stonewallx2Service = new STONEWALLx2Service(params);
        STONEWALLx2 payload0 = stonewallx2Service.startInitiator(cahootsWallet, amount, account, address);
        CahootsMessage cahootsMessage = new CahootsMessage(payload0);
        return cahootsMessage;
    }

    public CahootsMessage newStowaway(long amount) {
        StowawayService stowawayService = new StowawayService(params);
        Stowaway payload0 = stowawayService.startInitiator(cahootsWallet, amount, account);
        CahootsMessage cahootsMessage = new CahootsMessage(payload0);
        return cahootsMessage;
    }

    public CahootsMessage reply(String request) throws Exception {
        Cahoots cahoots = Cahoots.parse(request);
        CahootsMessage cahootsMessage = new CahootsMessage(cahoots);
        return reply(cahootsMessage);
    }

    public CahootsMessage reply(CahootsMessage request) throws Exception {
        Cahoots payload = request.getCahoots();
        AbstractCahootsService cahootsService = newCahootsService(payload.getType());

        Cahoots responsePayload;
        if (payload.getStep() == 0) {
            // new Cahoots as counterparty/receiver
            responsePayload = cahootsService.startCollaborator(payload, cahootsWallet, account);
        } else {
            // continue existing Cahoots
            responsePayload = cahootsService.reply(payload, cahootsWallet, feePerB);
        }
        CahootsMessage cahootsMessage = new CahootsMessage(responsePayload);
        return cahootsMessage;
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
}
