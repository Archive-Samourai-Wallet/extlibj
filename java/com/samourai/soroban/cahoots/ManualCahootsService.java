package com.samourai.soroban.cahoots;

import com.samourai.soroban.client.SorobanInteraction;
import com.samourai.soroban.client.SorobanMessageService;
import com.samourai.soroban.client.SorobanReply;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.cahoots.*;
import com.samourai.wallet.cahoots.multi.MultiCahoots;
import com.samourai.wallet.cahoots.multi.MultiCahootsService;
import com.samourai.wallet.cahoots.stonewallx2.STONEWALLx2;
import com.samourai.wallet.cahoots.stonewallx2.Stonewallx2Service;
import com.samourai.wallet.cahoots.stowaway.Stowaway;
import com.samourai.wallet.cahoots.stowaway.StowawayService;
import com.samourai.xmanager.client.XManagerClient;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualCahootsService extends SorobanMessageService<ManualCahootsMessage, CahootsContext> {
    private static final Logger log = LoggerFactory.getLogger(ManualCahootsService.class);

    private CahootsWallet cahootsWallet;
    private StowawayService stowawayService;
    private Stonewallx2Service stonewallx2Service;
    private MultiCahootsService multiCahootsService;

    public ManualCahootsService(CahootsWallet cahootsWallet, StowawayService stowawayService, Stonewallx2Service stonewallx2Service, MultiCahootsService multiCahootsService) {
        this.cahootsWallet = cahootsWallet;
        this.stowawayService = stowawayService;
        this.stonewallx2Service = stonewallx2Service;
        this.multiCahootsService = multiCahootsService;
    }

    public ManualCahootsService(CahootsWallet cahootsWallet, XManagerClient xManagerClient) {
        BipFormatSupplier bipFormatSupplier = cahootsWallet.getBipFormatSupplier();
        NetworkParameters params = cahootsWallet.getParams();

        this.cahootsWallet = cahootsWallet;
        this.stowawayService = new StowawayService(bipFormatSupplier, params);
        this.stonewallx2Service = new Stonewallx2Service(bipFormatSupplier, params);
        this.multiCahootsService = new MultiCahootsService(bipFormatSupplier, params, stonewallx2Service, stowawayService, xManagerClient);
    }

    public ManualCahootsMessage initiate(int account, CahootsContext cahootsContext) throws Exception {
        AbstractCahootsService cahootsService = getCahootsService(cahootsContext.getCahootsType());
        Cahoots payload0 = cahootsService.startInitiator(cahootsWallet, account, cahootsContext);
        ManualCahootsMessage response = new ManualCahootsMessage(payload0);

        verifyResponse(cahootsContext, response, cahootsService, null);
        return response;
    }

    @Override
    public ManualCahootsMessage parse(String payload) throws Exception{
        return ManualCahootsMessage.parse(payload);
    }

    public SorobanReply reply(int account, ManualCahootsMessage request) throws Exception {
        return reply(account, null, request);
    }

    @Override
    public SorobanReply reply(int account, CahootsContext cahootsContext, ManualCahootsMessage request) throws Exception {
        if (cahootsContext != null) {
            verifyRequest(cahootsContext, request);
        }

        final AbstractCahootsService cahootsService = getCahootsService(request.getType());
        final Cahoots payload = request.getCahoots();
        SorobanReply response;
        if (payload.getStep() == 0) {
            // new Cahoots as counterparty/receiver
            Cahoots cahootsResponse = cahootsService.startCollaborator(cahootsWallet, account, payload);
            response = new ManualCahootsMessage(cahootsResponse);
        } else {
            // continue existing Cahoots
            Cahoots cahootsResponse = cahootsService.reply(cahootsWallet, cahootsContext, payload);

            // check for interaction
            SorobanInteraction interaction = cahootsService.checkInteraction(request, cahootsResponse);
            if (interaction != null) {
                // reply interaction
                response = interaction;
            } else {
                // standard reply
                response = new ManualCahootsMessage(cahootsResponse);
                if (cahootsContext != null) {
                    verifyResponse(cahootsContext, (ManualCahootsMessage)response, cahootsService, request);
                }
            }
        }
        return response;
    }

    private void verifyRequest(CahootsContext sorobanContext, ManualCahootsMessage message) throws Exception {
        CahootsTypeUser typeUserExpected = sorobanContext.getTypeUser().getPartner();
        doVerify(sorobanContext, message, typeUserExpected);
    }

    private void verifyResponse(CahootsContext cahootsContext, ManualCahootsMessage response, AbstractCahootsService cahootsService, ManualCahootsMessage request) throws Exception {
        CahootsTypeUser typeUserExpected = cahootsContext.getTypeUser();
        doVerify(cahootsContext, response, typeUserExpected);

        cahootsService.verifyResponse(cahootsContext, response.getCahoots(), (request!=null?request.getCahoots():null));
    }

    private void doVerify(CahootsContext cahootsContext, ManualCahootsMessage message, CahootsTypeUser typeUserExpected) throws Exception {
        Cahoots cahoots = message.getCahoots();

        // check type
        CahootsType cahootsType = CahootsType.find(cahoots.getType()).get();
        if (!cahootsType.equals(cahootsContext.getCahootsType())) {
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
            case MULTI:
                if(!(cahoots instanceof MultiCahoots)) {
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

    private AbstractCahootsService getCahootsService(CahootsType cahootsType) throws Exception {
        switch(cahootsType) {
            case STOWAWAY:
                return stowawayService;
            case STONEWALLX2:
                return stonewallx2Service;
            case MULTI:
                return multiCahootsService;
        }
        throw new Exception("Unrecognized #Cahoots");
    }
}
