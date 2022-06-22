package com.samourai.wallet.cahoots.multi;

import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.cahoots.AbstractCahootsService;
import com.samourai.wallet.cahoots.CahootsUtxo;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.cahoots.stonewallx2.STONEWALLx2;
import com.samourai.wallet.cahoots.stonewallx2.Stonewallx2Service;
import com.samourai.wallet.cahoots.stowaway.Stowaway;
import com.samourai.wallet.cahoots.stowaway.StowawayService;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MultiCahootsService extends AbstractCahootsService<MultiCahoots> {
    private static final Logger log = LoggerFactory.getLogger(MultiCahootsService.class);
    private Stonewallx2Service stonewallx2Service;
    private StowawayService stowawayService;

    public MultiCahootsService(BipFormatSupplier bipFormatSupplier, NetworkParameters params, Stonewallx2Service stonewallx2Service, StowawayService stowawayService) {
        super(bipFormatSupplier, params);
        this.stonewallx2Service = stonewallx2Service;
        this.stowawayService = stowawayService;
    }

    public MultiCahoots startInitiator(CahootsWallet cahootsWallet, String address, long amount, int account) throws Exception {
        long stowawayFee = (long)(amount * 0.035d) + 400;
        if(stowawayFee > 1000000) {
            stowawayFee = 1000000;
        }
        Stowaway stowaway0 = stowawayService.startInitiator(cahootsWallet, stowawayFee, account);
        STONEWALLx2 stonewall0 = stonewallx2Service.startInitiator(cahootsWallet, amount, account, address);

        MultiCahoots multiCahoots0 = new MultiCahoots(amount, params, account, stowaway0, stonewall0);
        return multiCahoots0;
    }

    @Override
    public MultiCahoots startCollaborator(CahootsWallet cahootsWallet, int account, MultiCahoots stowaway0) throws Exception {
        MultiCahoots stowaway1 = doMultiCahoots1_Stowaway1(stowaway0, cahootsWallet, account);
        if (log.isDebugEnabled()) {
            log.debug("# Stowaway COUNTERPARTY => step="+stowaway1.getStep());
        }
        return stowaway1;
    }

    @Override
    public MultiCahoots reply(CahootsWallet cahootsWallet, MultiCahoots multiCahoots) throws Exception {
        int step = multiCahoots.getStep();
        if (log.isDebugEnabled()) {
            log.debug("# Stowaway <= step="+step);
        }
        MultiCahoots payload;
        switch (step) {
            case 1:
                // sender
                payload = doMultiCahoots2_Stowaway2(multiCahoots, cahootsWallet);
                break;
            case 2:
                // counterparty
                payload = doMultiCahoots3_Stowaway3(multiCahoots, cahootsWallet);
                break;
            case 3:
                // sender
                payload = doMultiCahoots4_Stowaway4(multiCahoots, cahootsWallet);
                break;
            case 4:
                // counterparty
                payload = doMultiCahoots5_Stonewallx21_StartCollaborator(multiCahoots, cahootsWallet, multiCahoots.getAccount());
                break;
            case 5:
                // sender
                payload = doMultiCahoots6_Stonewallx22(multiCahoots, cahootsWallet);
                break;
            case 6:
                // counterparty
                payload = doMultiCahoots7_Stonewallx23(multiCahoots, cahootsWallet);
                break;
            case 7:
                // sender
                payload = doMultiCahoots8_Stonewallx24(multiCahoots, cahootsWallet);
                break;
            default:
                throw new Exception("Unrecognized #Cahoots step");
        }
        if (payload == null) {
            throw new Exception("Cannot compose #Cahoots");
        }
        if (log.isDebugEnabled()) {
            log.debug("# Stowaway => step="+payload.getStep());
        }
        return payload;
    }

    //
    // counterparty
    //
    private MultiCahoots doMultiCahoots1_Stowaway1(MultiCahoots multiCahoots0, CahootsWallet cahootsWallet, int account) throws Exception {
        Stowaway stowaway1 = stowawayService.doStowaway1(multiCahoots0.getStowaway(), cahootsWallet, account);

        MultiCahoots multiCahoots1 = new MultiCahoots(multiCahoots0);
        multiCahoots1.setStowaway(stowaway1);
        multiCahoots1.setStep(1);
        return multiCahoots1;
    }

    //
    // sender
    //
    private MultiCahoots doMultiCahoots2_Stowaway2(MultiCahoots multiCahoots1, CahootsWallet cahootsWallet) throws Exception {
        Stowaway stowaway2 = stowawayService.doStowaway2(multiCahoots1.getStowaway(), cahootsWallet);

        MultiCahoots multiCahoots2 = new MultiCahoots(multiCahoots1);
        multiCahoots2.setStowaway(stowaway2);
        multiCahoots2.setStep(2);
        return multiCahoots2;
    }

    //
    // counterparty
    //
    private MultiCahoots doMultiCahoots3_Stowaway3(MultiCahoots multiCahoots2, CahootsWallet cahootsWallet) throws Exception {
        Stowaway stowaway3 = stowawayService.doStowaway3(multiCahoots2.getStowaway(), cahootsWallet);

        MultiCahoots multiCahoots3 = new MultiCahoots(multiCahoots2);
        multiCahoots3.setStowaway(stowaway3);
        multiCahoots3.setStep(3);
        return multiCahoots3;
    }

    //
    // sender
    //
    private MultiCahoots doMultiCahoots4_Stowaway4(MultiCahoots multiCahoots3, CahootsWallet cahootsWallet) throws Exception {
        Stowaway stowaway4 = stowawayService.doStowaway4(multiCahoots3.getStowaway(), cahootsWallet);

        MultiCahoots multiCahoots4 = new MultiCahoots(multiCahoots3);
        multiCahoots4.setStowaway(stowaway4);
        multiCahoots4.setStep(4);
        return multiCahoots4;
    }

    //
    // counterparty
    //
    public MultiCahoots doMultiCahoots5_Stonewallx21_StartCollaborator(MultiCahoots multiCahoots4, CahootsWallet cahootsWallet, int account) throws Exception {
        // finalize stowaway: this sets stowaway transaction
        Stowaway stowaway5 = new Stowaway(multiCahoots4.getStowaway());

        // start stonewallx2 as collaborator
        List<String> seenTxs = new ArrayList<String>();
        for(TransactionInput input : multiCahoots4.getStowaway().getTransaction().getInputs()) {
            seenTxs.add(input.getOutpoint().getHash().toString());
        }
        STONEWALLx2 stonewall1 = stonewallx2Service.doSTONEWALLx2_1_Multi(multiCahoots4.getStonewallx2(), cahootsWallet, account, seenTxs);

        MultiCahoots multiCahoots5 = new MultiCahoots(multiCahoots4);
        multiCahoots5.setStowaway(stowaway5);
        multiCahoots5.setStonewallx2(stonewall1);
        multiCahoots5.setStep(5);

        return multiCahoots5;
    }

    //
    // sender - new
    //
    private MultiCahoots doMultiCahoots6_Stonewallx22(MultiCahoots multiCahoots5, CahootsWallet cahootsWallet) throws Exception {
        List<String> seenTxs = new ArrayList<String>();
        for (TransactionInput input : multiCahoots5.getStonewallx2().getTransaction().getInputs()) {
            if (!seenTxs.contains(input.getOutpoint().getHash().toString())) {
                seenTxs.add(input.getOutpoint().getHash().toString());
            }
        }

        for(TransactionInput input : multiCahoots5.getStowaway().getTransaction().getInputs()) {
            seenTxs.add(input.getOutpoint().getHash().toString());
        }
        STONEWALLx2 stonewall2 = stonewallx2Service.doSTONEWALLx2_2(multiCahoots5.getStonewallx2(), cahootsWallet, seenTxs);

        MultiCahoots multiCahoots6 = new MultiCahoots(multiCahoots5);
        multiCahoots6.setStonewallx2(stonewall2);
        multiCahoots6.setStep(6);
        return multiCahoots6;
    }

    //
    // counterparty - new
    //
    private MultiCahoots doMultiCahoots7_Stonewallx23(MultiCahoots multiCahoots6, CahootsWallet cahootsWallet) throws Exception {
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(multiCahoots6.getCounterpartyAccount());

        boolean noExtraFee = checkForNoExtraFee(multiCahoots6.getStonewallx2(), utxos);
        if(!noExtraFee) {
            throw new Exception("Cannot compose #Cahoots: extra fee is being taken from us");
        }
        STONEWALLx2 stonewall3 = stonewallx2Service.doSTONEWALLx2_3(multiCahoots6.getStonewallx2(), cahootsWallet);

        MultiCahoots multiCahoots7 = new MultiCahoots(multiCahoots6);
        multiCahoots7.setStonewallx2(stonewall3);
        multiCahoots7.setStep(7);
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
                e.printStackTrace();
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
    // sender - new
    //
    private MultiCahoots doMultiCahoots8_Stonewallx24(MultiCahoots multiCahoots7, CahootsWallet cahootsWallet) throws Exception {
        STONEWALLx2 stonewall4 = stonewallx2Service.doSTONEWALLx2_4(multiCahoots7.getStonewallx2(), cahootsWallet);

        MultiCahoots multiCahoots8 = new MultiCahoots(multiCahoots7);
        multiCahoots8.setStonewallx2(stonewall4);
        multiCahoots8.setStep(8);
        return multiCahoots8;
    }
}
