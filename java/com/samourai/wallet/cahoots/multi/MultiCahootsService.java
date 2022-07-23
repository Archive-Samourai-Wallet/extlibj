package com.samourai.wallet.cahoots.multi;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.TypeInteraction;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.cahoots.AbstractCahootsService;
import com.samourai.wallet.cahoots.CahootsType;
import com.samourai.wallet.cahoots.CahootsUtxo;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.cahoots.stonewallx2.STONEWALLx2;
import com.samourai.wallet.cahoots.stonewallx2.Stonewallx2Service;
import com.samourai.wallet.cahoots.stowaway.Stowaway;
import com.samourai.wallet.cahoots.stowaway.StowawayService;
import com.samourai.wallet.util.TxUtil;
import com.samourai.xmanager.client.XManagerClient;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MultiCahootsService extends AbstractCahootsService<MultiCahoots, MultiCahootsContext> {
    private static final Logger log = LoggerFactory.getLogger(MultiCahootsService.class);
    private Stonewallx2Service stonewallx2Service;
    private StowawayService stowawayService;
    private XManagerClient xManagerClient;

    public MultiCahootsService(BipFormatSupplier bipFormatSupplier, NetworkParameters params, Stonewallx2Service stonewallx2Service, StowawayService stowawayService, XManagerClient xManagerClient) {
        super(CahootsType.MULTI, bipFormatSupplier, params, TypeInteraction.TX_BROADCAST_MULTI);
        this.stonewallx2Service = stonewallx2Service;
        this.stowawayService = stowawayService;
        this.xManagerClient = xManagerClient;
    }

    @Override
    public MultiCahoots startInitiator(CahootsWallet cahootsWallet, MultiCahootsContext cahootsContext) throws Exception {
        CahootsContext stowawayContext = cahootsContext.getStowawayContext();
        Stowaway stowaway0 = stowawayService.startInitiator(cahootsWallet, stowawayContext);

        CahootsContext stonewallContext = cahootsContext.getStonewallx2Context();
        STONEWALLx2 stonewall0 = stonewallx2Service.startInitiator(cahootsWallet, stonewallContext);

        MultiCahoots multiCahoots0 = new MultiCahoots(params, stowaway0, stonewall0);
        return multiCahoots0;
    }

    @Override
    public MultiCahoots startCollaborator(CahootsWallet cahootsWallet, MultiCahootsContext cahootsContext, MultiCahoots stowaway0) throws Exception {
        MultiCahoots stowaway1 = doMultiCahoots1_Stowaway1(stowaway0, cahootsWallet, cahootsContext);
        if (log.isDebugEnabled()) {
            log.debug("# MultiCahoots COUNTERPARTY => step="+stowaway1.getStep());
        }
        return stowaway1;
    }

    @Override
    public MultiCahoots reply(CahootsWallet cahootsWallet, MultiCahootsContext cahootsContext, MultiCahoots multiCahoots) throws Exception {
        int step = multiCahoots.getStep();
        if (log.isDebugEnabled()) {
            log.debug("# MultiCahoots <= step="+step);
        }
        MultiCahoots payload;
        switch (step) {
            case 1:
                // sender
                payload = doMultiCahoots2_Stowaway2(multiCahoots, cahootsWallet, cahootsContext);
                break;
            case 2:
                // counterparty
                payload = doMultiCahoots3_Stowaway3_Stonewallx21(multiCahoots, cahootsWallet, cahootsContext);
                break;
            case 3:
                // sender
                payload = doMultiCahoots4_Stowaway4_Stonewallx22(multiCahoots, cahootsWallet, cahootsContext);
                break;
            case 4:
                // counterparty
                payload = doMultiCahoots5_Stonewallx23(multiCahoots, cahootsWallet, cahootsContext);
                break;
            case 5:
                // sender
                payload = doMultiCahoots6_Stonewallx24(multiCahoots, cahootsWallet, cahootsContext);
                break;
            default:
                throw new Exception("Unrecognized #Cahoots step");
        }
        if (payload == null) {
            throw new Exception("Cannot compose #Cahoots");
        }
        if (log.isDebugEnabled()) {
            log.debug("# MultiCahoots => step="+payload.getStep());
        }
        return payload;
    }

    //
    // counterparty
    //
    private MultiCahoots doMultiCahoots1_Stowaway1(MultiCahoots multiCahoots0, CahootsWallet cahootsWallet, MultiCahootsContext cahootsContext) throws Exception {
        CahootsContext stowawayContext = cahootsContext.getStowawayContext();
        Stowaway stowaway1 = stowawayService.doStowaway1(multiCahoots0.getStowaway(), cahootsWallet, stowawayContext);

        MultiCahoots multiCahoots1 = new MultiCahoots(multiCahoots0);
        multiCahoots1.setStowaway(stowaway1);
        multiCahoots1.setStep(1);
        return multiCahoots1;
    }

    //
    // sender
    //
    private MultiCahoots doMultiCahoots2_Stowaway2(MultiCahoots multiCahoots1, CahootsWallet cahootsWallet, MultiCahootsContext cahootsContext) throws Exception {
        CahootsContext stowawayContext = cahootsContext.getStowawayContext();
        Stowaway stowaway2 = stowawayService.doStowaway2(multiCahoots1.getStowaway(), cahootsWallet, stowawayContext);

        MultiCahoots multiCahoots2 = new MultiCahoots(multiCahoots1);
        multiCahoots2.setStowaway(stowaway2);
        multiCahoots2.setStep(2);
        return multiCahoots2;
    }

    //
    // counterparty
    //
    private MultiCahoots doMultiCahoots3_Stowaway3_Stonewallx21(MultiCahoots multiCahoots2, CahootsWallet cahootsWallet, MultiCahootsContext cahootsContext) throws Exception {
        // continue stowaway
        CahootsContext stowawayContext = cahootsContext.getStowawayContext();
        Stowaway stowaway3 = stowawayService.doStep3(multiCahoots2.getStowaway(), cahootsWallet, stowawayContext);

        // start stonewallx2 as collaborator
        List<String> seenTxs = new ArrayList<>();
        for(TransactionInput input : multiCahoots2.getStowaway().getTransaction().getInputs()) {
            seenTxs.add(input.getOutpoint().getHash().toString());
        }
        CahootsContext stonewallContext = cahootsContext.getStonewallx2Context();
        STONEWALLx2 stonewall1 = stonewallx2Service.doSTONEWALLx2_1_Multi(multiCahoots2.getStonewallx2(), cahootsWallet, stonewallContext, seenTxs, xManagerClient);

        MultiCahoots multiCahoots3 = new MultiCahoots(multiCahoots2);
        multiCahoots3.setStowaway(stowaway3);
        multiCahoots3.setStonewallx2(stonewall1);
        multiCahoots3.setStep(3);
        return multiCahoots3;
    }

    //
    // sender
    //
    private MultiCahoots doMultiCahoots4_Stowaway4_Stonewallx22(MultiCahoots multiCahoots3, CahootsWallet cahootsWallet, MultiCahootsContext cahootsContext) throws Exception {
        // continue stowaway
        CahootsContext stowawayContext = cahootsContext.getStowawayContext();
        Stowaway stowaway4 = stowawayService.doStep4(multiCahoots3.getStowaway(), cahootsWallet, stowawayContext);

        // continue stonewallx2
        List<String> seenTxs = new ArrayList<String>();
        for (TransactionInput input : multiCahoots3.getStonewallx2().getTransaction().getInputs()) {
            if (!seenTxs.contains(input.getOutpoint().getHash().toString())) {
                seenTxs.add(input.getOutpoint().getHash().toString());
            }
        }
        for(TransactionInput input : multiCahoots3.getStowaway().getTransaction().getInputs()) {
            seenTxs.add(input.getOutpoint().getHash().toString());
        }
        CahootsContext stonewallContext = cahootsContext.getStonewallx2Context();
        STONEWALLx2 stonewall2 = stonewallx2Service.doSTONEWALLx2_2(multiCahoots3.getStonewallx2(), cahootsWallet, stonewallContext, seenTxs);

        MultiCahoots multiCahoots4 = new MultiCahoots(multiCahoots3);
        multiCahoots4.setStowaway(stowaway4);
        multiCahoots4.setStonewallx2(stonewall2);
        multiCahoots4.setStep(4);
        return multiCahoots4;
    }

    //
    // counterparty
    //
    private MultiCahoots doMultiCahoots5_Stonewallx23(MultiCahoots multiCahoots6, CahootsWallet cahootsWallet, MultiCahootsContext cahootsContext) throws Exception {
        int account = multiCahoots6.getStonewallx2().getCounterpartyAccount();
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(account);

        boolean noExtraFee = checkForNoExtraFee(multiCahoots6.getStonewallx2(), utxos);
        if(!noExtraFee) {
            throw new Exception("Cannot compose #Cahoots: extra fee is being taken from us");
        }
        CahootsContext stonewallContext = cahootsContext.getStonewallx2Context();
        STONEWALLx2 stonewall3 = stonewallx2Service.doStep3(multiCahoots6.getStonewallx2(), cahootsWallet, stonewallContext);

        MultiCahoots multiCahoots7 = new MultiCahoots(multiCahoots6);
        multiCahoots7.setStonewallx2(stonewall3);
        multiCahoots7.setStep(5);
        return multiCahoots7;
    }

    private boolean checkForNoExtraFee(STONEWALLx2 stonewallx2, List<CahootsUtxo> utxos) {
        long inputSum = 0;
        long outputSum = 0;

        for(int i = 0; i < stonewallx2.getTransaction().getInputs().size(); i++) {
            TransactionInput input = stonewallx2.getTransaction().getInput(i);
            for(CahootsUtxo cahootsUtxo : utxos) {
                int outpointIndex = cahootsUtxo.getOutpoint().getTxOutputN();
                Sha256Hash outpointHash = cahootsUtxo.getOutpoint().getTxHash();
                if(input != null && input.getOutpoint().getHash().equals(outpointHash) && input.getOutpoint().getIndex() == outpointIndex) {
                    long amount = cahootsUtxo.getValue();
                    inputSum += amount;
                    break;
                }
            }
        }
        for(int i = 0; i < stonewallx2.getTransaction().getOutputs().size(); i++) {
            TransactionOutput utxo = stonewallx2.getTransaction().getOutput(i);
            long amount = utxo.getValue().value;
            String address = null;
            try {
                address = getBipFormatSupplier().getToAddress(utxo);
            } catch (Exception e) {
                log.error("", e);
            }
            if(address != null && address.equals(stonewallx2.getCollabChange())) {
                outputSum += amount;
            } else if(address != null && amount == stonewallx2.getSpendAmount() && !address.equals(stonewallx2.getDestination())) {
                outputSum += amount;
            }
        }

        return (inputSum - outputSum) == (stonewallx2.getFeeAmount()/2L) && inputSum != 0 && outputSum != 0;
    }

    //
    // sender
    //
    private MultiCahoots doMultiCahoots6_Stonewallx24(MultiCahoots multiCahoots7, CahootsWallet cahootsWallet, MultiCahootsContext cahootsContext) throws Exception {
        CahootsContext stonewallContext = cahootsContext.getStonewallx2Context();
        STONEWALLx2 stonewall4 = stonewallx2Service.doStep4(multiCahoots7.getStonewallx2(), cahootsWallet, stonewallContext);

        MultiCahoots multiCahoots8 = new MultiCahoots(multiCahoots7);
        multiCahoots8.setStonewallx2(stonewall4);
        multiCahoots8.setStep(6);
        return multiCahoots8;
    }

    @Override
    public void verifyResponse(MultiCahootsContext cahootsContext, MultiCahoots multiCahoots, MultiCahoots request) throws Exception {
        super.verifyResponse(cahootsContext, multiCahoots, request);

        if (multiCahoots.getStep() <= 4) {
            // validate stowaway
            CahootsContext stowawayContext = cahootsContext.getStowawayContext();
            stowawayService.verifyResponse(stowawayContext, multiCahoots.stowaway, (request!=null?request.stowaway:null));
        } else {
            // stowaway should keep unchanged once finished
            if (!TxUtil.getInstance().getTxHex(multiCahoots.getStowawayTransaction())
                    .equals(TxUtil.getInstance().getTxHex(request.getStowawayTransaction()))) {
                throw new Exception("Invalid alterated stowaway tx");
            }
        }

        if (multiCahoots.getStep() >= 3) {
            // validate stonewallx2
            CahootsContext stonewallContext = cahootsContext.getStonewallx2Context();
            stonewallx2Service.verifyResponse(stonewallContext, multiCahoots.stonewallx2, request.stonewallx2);
        }
    }
}