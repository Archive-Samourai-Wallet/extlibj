package com.samourai.wallet.soroban.cahoots;

import com.samourai.wallet.cahoots.*;
import com.samourai.wallet.cahoots.stonewallx2.STONEWALLx2;
import com.samourai.wallet.cahoots.stonewallx2.Stonewallx2Service;
import com.samourai.wallet.cahoots.stowaway.Stowaway;
import com.samourai.wallet.cahoots.stowaway.StowawayService;
import com.samourai.wallet.soroban.client.SorobanInteraction;
import com.samourai.wallet.soroban.client.SorobanMessage;
import com.samourai.wallet.soroban.client.SorobanMessageService;
import com.samourai.wallet.soroban.client.SorobanReply;
import java8.util.Optional;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class ManualCahootsService extends SorobanMessageService<ManualCahootsMessage, CahootsContext> {
    private static final Logger log = LoggerFactory.getLogger(ManualCahootsService.class);

    private CahootsWallet cahootsWallet;

    public ManualCahootsService(CahootsWallet cahootsWallet) {
        this.cahootsWallet = cahootsWallet;
    }

    public ManualCahootsMessage initiate(int account, CahootsContext cahootsContext) throws Exception {
        AbstractCahootsService cahootsService = newCahootsService(cahootsContext.getCahootsType());
        Cahoots payload0;
        switch(cahootsContext.getCahootsType()) {
            case STOWAWAY:
                payload0 = ((StowawayService)cahootsService).startInitiator(cahootsWallet, cahootsContext.getAmount(), account);
                break;
            case STONEWALLX2:
                payload0 = ((Stonewallx2Service)cahootsService).startInitiator(cahootsWallet, cahootsContext.getAmount(), account, cahootsContext.getAddress());
                break;
            default:
                throw new Exception("Unknown Cahoots type");
        }
        ManualCahootsMessage response = new ManualCahootsMessage(payload0);

        verifyResponse(cahootsContext, response);
        return response;
    }

    @Override
    public ManualCahootsMessage parse(String payload) throws Exception{
        return ManualCahootsMessage.parse(payload);
    }

    @Override
    public SorobanReply reply(int account, CahootsContext cahootsContext, ManualCahootsMessage request) throws Exception {
        verifyRequest(cahootsContext, request);

        final AbstractCahootsService cahootsService = newCahootsService(request.getType());
        final Cahoots payload = request.getCahoots();
        SorobanReply response;
        if (payload.getStep() == 0) {
            // new Cahoots as counterparty/receiver
            Cahoots cahootsResponse = cahootsService.startCollaborator(cahootsWallet, account, payload);
            response = new ManualCahootsMessage(cahootsResponse);
        } else {
            // continue existing Cahoots

            // check for interaction
            Optional<TypeInteraction> optInteraction =
                    TypeInteraction.find(request.getTypeUser().getPartner(), request.getStep() + 1);
            if (optInteraction.isPresent()) {
                final TypeInteraction typeInteraction = optInteraction.get();
                // reply interaction
                Callable<SorobanMessage> onAccept =
                    new Callable<SorobanMessage>() {
                        @Override
                        public SorobanMessage call() throws Exception {
                            Cahoots cahootsResponse = cahootsService.reply(cahootsWallet, payload);
                            return new ManualCahootsMessage(cahootsResponse);
                        }
                    };
                response = new SorobanInteraction(request, typeInteraction, onAccept);
            } else {
                // direct reply
                Cahoots cahootsResponse = cahootsService.reply(cahootsWallet, payload);
                response = new ManualCahootsMessage(cahootsResponse);
            }
        }
        if (!(response instanceof SorobanInteraction)) {
            verifyResponse(cahootsContext, (ManualCahootsMessage)response);
        }
        return response;
    }

    private void verifyRequest(CahootsContext sorobanContext, ManualCahootsMessage message) throws Exception {
        CahootsTypeUser typeUserExpected = sorobanContext.getTypeUser().getPartner();
        doVerify(sorobanContext, message, typeUserExpected);
    }

    private void verifyResponse(CahootsContext sorobanContext, ManualCahootsMessage message) throws Exception {
        CahootsTypeUser typeUserExpected = sorobanContext.getTypeUser();
        doVerify(sorobanContext, message, typeUserExpected);
    }

    private void doVerify(CahootsContext sorobanContext, ManualCahootsMessage message, CahootsTypeUser typeUserExpected) throws Exception {
        Cahoots cahoots = message.getCahoots();

        // check type
        CahootsType cahootsType = CahootsType.find(cahoots.getType()).get();
        if (!cahootsType.equals(sorobanContext.getCahootsType())) {
            throw new Exception("Cahoots type mismatch");
        }
        switch (cahootsType) {
            case STONEWALLX2:
                if (!(cahoots instanceof STONEWALLx2)) {
                    throw new Exception("Cahoots instance type mismatch");
                }
                break;
            case STOWAWAY:
                if (!(cahoots instanceof Stowaway)) {
                    throw new Exception("Cahoots instance type mismatch");
                }
                break;
            default:
                throw new Exception("Unknown Cahoots type");
        }

        // check typeUser
        if (message.getTypeUser() == null) {
            throw new Exception("Cahoots typeUser required");
        }
        if (!message.getTypeUser().equals(typeUserExpected)) {
            throw new Exception("Cahoots typeUser mismatch");
        }
    }

    private AbstractCahootsService newCahootsService(CahootsType cahootsType) throws Exception {
        NetworkParameters params = cahootsWallet.getParams();
        switch(cahootsType) {
            case STOWAWAY:
                return new StowawayService(params);
            case STONEWALLX2:
                return new Stonewallx2Service(params);
        }
        throw new Exception("Unrecognized #Cahoots");
    }
}
