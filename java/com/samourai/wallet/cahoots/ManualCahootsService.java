package com.samourai.wallet.cahoots;

import com.samourai.wallet.soroban.client.SorobanMessageService;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualCahootsService extends SorobanMessageService<ManualCahootsMessage> {
    private static final Logger log = LoggerFactory.getLogger(ManualCahootsService.class);

    private NetworkParameters params;
    private CahootsWallet cahootsWallet;

    public ManualCahootsService(NetworkParameters params, CahootsWallet cahootsWallet) {
        this.params = params;
        this.cahootsWallet = cahootsWallet;
    }

    @Override
    public ManualCahootsMessage parse(String payload) throws Exception{
        return ManualCahootsMessage.parse(payload);
    }

    public ManualCahootsMessage newStonewallx2(int account, long amount, String address) throws Exception {
        STONEWALLx2Service stonewallx2Service = new STONEWALLx2Service(params);
        STONEWALLx2 payload0 = stonewallx2Service.startInitiator(cahootsWallet, amount, account, address);
        ManualCahootsMessage cahootsMessage = new ManualCahootsMessage(payload0);
        return cahootsMessage;
    }

    public ManualCahootsMessage newStowaway(int account, long amount) throws Exception {
        StowawayService stowawayService = new StowawayService(params);
        Stowaway payload0 = stowawayService.startInitiator(cahootsWallet, amount, account);
        ManualCahootsMessage cahootsMessage = new ManualCahootsMessage(payload0);
        return cahootsMessage;
    }

    @Override
    public ManualCahootsMessage reply(int account, ManualCahootsMessage request) throws Exception {
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
        ManualCahootsMessage cahootsMessage = new ManualCahootsMessage(responsePayload);
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
