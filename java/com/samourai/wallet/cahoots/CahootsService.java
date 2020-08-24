package com.samourai.wallet.cahoots;

import com.samourai.wallet.soroban.client.SorobanMessageService;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CahootsService extends SorobanMessageService<CahootsMessage> {
    private static final Logger log = LoggerFactory.getLogger(CahootsService.class);

    private NetworkParameters params;
    private CahootsWallet cahootsWallet;
    private int account;

    public CahootsService(NetworkParameters params, CahootsWallet cahootsWallet, int account) {
        this.params = params;
        this.cahootsWallet = cahootsWallet;
        this.account = account;
    }

    @Override
    public CahootsMessage parse(String payload) throws Exception{
        return CahootsMessage.parse(payload);
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

    @Override
    public CahootsMessage reply(CahootsMessage request) throws Exception {
        Cahoots payload = request.getCahoots();
        AbstractCahootsService cahootsService = newCahootsService(request.getType());

        Cahoots responsePayload;
        if (payload.getStep() == 0) {
            // new Cahoots as counterparty/receiver
            responsePayload = cahootsService.startCollaborator(payload, cahootsWallet, account);
        } else {
            // continue existing Cahoots
            responsePayload = cahootsService.reply(payload, cahootsWallet);
        }
        CahootsMessage cahootsMessage = new CahootsMessage(responsePayload);
        return cahootsMessage;
    }

    private AbstractCahootsService newCahootsService(CahootsType cahootsType) throws Exception {
        switch(cahootsType) {
            case STOWAWAY:
                return new StowawayService(params);
            case STONEWALLX2:
                return new STONEWALLx2Service(params);
        }
        throw new Exception("Unrecognized #Cahoots");
    }
}
