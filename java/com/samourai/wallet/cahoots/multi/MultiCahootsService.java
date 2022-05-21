package com.samourai.wallet.cahoots.multi;

import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.cahoots.AbstractCahootsService;
import com.samourai.wallet.cahoots.CahootsUtxo;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.cahoots.stonewallx2.STONEWALLx2;
import com.samourai.wallet.cahoots.stonewallx2.Stonewallx2Service;
import com.samourai.wallet.cahoots.stowaway.Stowaway;
import com.samourai.wallet.cahoots.stowaway.StowawayService;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.TransactionInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MultiCahootsService extends AbstractCahootsService<MultiCahoots> {
    private static final Logger log = LoggerFactory.getLogger(MultiCahootsService.class);
    private static final Bech32UtilGeneric bech32Util = Bech32UtilGeneric.getInstance();
    private Stonewallx2Service stonewallx2Service;
    private StowawayService stowawayService;

    public MultiCahootsService(BipFormatSupplier bipFormatSupplier, NetworkParameters params, Stonewallx2Service stonewallx2Service, StowawayService stowawayService) {
        super(bipFormatSupplier, params);
        this.stonewallx2Service = stonewallx2Service;
        this.stowawayService = stowawayService;
    }

    public MultiCahoots startInitiator(CahootsWallet cahootsWallet, String address, long amount, int account) throws Exception {
        long stowawayFee = (long)(amount * 0.01d);
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
    public MultiCahoots reply(CahootsWallet cahootsWallet, MultiCahoots stowaway) throws Exception {
        int step = stowaway.getStep();
        if (log.isDebugEnabled()) {
            log.debug("# Stowaway <= step="+step);
        }
        MultiCahoots payload;
        switch (step) {
            case 1:
                payload = doMultiCahoots2_Stowaway2(stowaway, cahootsWallet);
                break;
            case 2:
                payload = doMultiCahoots3_Stowaway3(stowaway, cahootsWallet);
                break;
            case 3:
                payload = doMultiCahoots4_Stowaway4(stowaway, cahootsWallet);
                break;
            case 4:
                payload = doMultiCahoots4_Stowaway_finalize(stowaway, cahootsWallet);
                break;
            case 5:
                payload = doMultiCahoots6_Stonewallx21_StartCollaborator(stowaway, cahootsWallet, stowaway.getAccount());
                break;
            case 6:
                payload = doMultiCahoots7_Stonewallx22(stowaway, cahootsWallet);
                break;
            case 7:
                payload = doMultiCahoots8_Stonewallx23(stowaway, cahootsWallet);
                break;
            case 8:
                payload = doMultiCahoots9_Stonewallx24(stowaway, cahootsWallet);
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
    // receiver
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
    // receiver
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
        Stowaway stowaway4 = stowawayService.doStowaway3(multiCahoots3.getStowaway(), cahootsWallet);

        MultiCahoots multiCahoots4 = new MultiCahoots(multiCahoots3);
        multiCahoots4.setStowaway(stowaway4);
        multiCahoots4.setStep(4);
        return multiCahoots4;
    }

    //
    // receiver
    //
    public MultiCahoots doMultiCahoots4_Stowaway_finalize(MultiCahoots multiCahoots4, CahootsWallet cahootsWallet) throws Exception {
        Stowaway stowaway5 = new Stowaway(multiCahoots4.getStowaway());

        MultiCahoots multiCahoots5 = new MultiCahoots(multiCahoots4);
        multiCahoots5.setStowaway(stowaway5); // this sets stowaway transaction
        multiCahoots5.setStep(5);
        return multiCahoots5;
    }

    //
    // counterparty
    //
    private MultiCahoots doMultiCahoots6_Stonewallx21_StartCollaborator(MultiCahoots multiCahoots5, CahootsWallet cahootsWallet, int account) throws Exception {
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(multiCahoots5.getCounterpartyAccount());
        List<CahootsUtxo> filteredUtxos = filterUtxosByInputs(utxos, multiCahoots5.getStowaway().getTransaction().getInputs());
        STONEWALLx2 stonewall1 = stonewallx2Service.doSTONEWALLx2_1(multiCahoots5.getStonewallx2(), cahootsWallet, account, filteredUtxos);

        MultiCahoots multiCahoots6 = new MultiCahoots(multiCahoots5);
        multiCahoots6.setStonewallx2(stonewall1);
        multiCahoots6.setStep(6);
        return multiCahoots6;
    }

    //
    // sender
    //
    private MultiCahoots doMultiCahoots7_Stonewallx22(MultiCahoots multiCahoots6, CahootsWallet cahootsWallet) throws Exception {
        List<CahootsUtxo> utxos = cahootsWallet.getUtxosWpkhByAccount(multiCahoots6.getAccount());
        List<CahootsUtxo> filteredUtxos = filterUtxosByInputs(utxos, multiCahoots6.getStowaway().getTransaction().getInputs());
        STONEWALLx2 stonewall2 = stonewallx2Service.doSTONEWALLx2_2(multiCahoots6.getStonewallx2(), cahootsWallet, filteredUtxos);

        MultiCahoots multiCahoots7 = new MultiCahoots(multiCahoots6);
        multiCahoots7.setStonewallx2(stonewall2);
        multiCahoots7.setStep(7);
        return multiCahoots7;
    }

    //
    // counterparty
    //
    private MultiCahoots doMultiCahoots8_Stonewallx23(MultiCahoots multiCahoots7, CahootsWallet cahootsWallet) throws Exception {
        STONEWALLx2 stonewall3 = stonewallx2Service.doSTONEWALLx2_3(multiCahoots7.getStonewallx2(), cahootsWallet);

        MultiCahoots multiCahoots8 = new MultiCahoots(multiCahoots7);
        multiCahoots8.setStonewallx2(stonewall3);
        multiCahoots8.setStep(8);
        return multiCahoots8;
    }

    //
    // sender
    //
    private MultiCahoots doMultiCahoots9_Stonewallx24(MultiCahoots multiCahoots8, CahootsWallet cahootsWallet) throws Exception {
        STONEWALLx2 stonewall4 = stonewallx2Service.doSTONEWALLx2_4(multiCahoots8.getStonewallx2(), cahootsWallet);

        MultiCahoots multiCahoots9 = new MultiCahoots(multiCahoots8);
        multiCahoots9.setStonewallx2(stonewall4);
        multiCahoots9.setStep(9);
        return multiCahoots9;
    }

    private List<CahootsUtxo> filterUtxosByInputs(Collection<CahootsUtxo> utxos, List<TransactionInput> excludeInputs) {
        List<CahootsUtxo> filteredUtxos = new ArrayList<>(utxos);
        for(CahootsUtxo utxo : utxos) {
            for(TransactionInput input : excludeInputs) {
                if(input.getOutpoint().getHash().equals(utxo.getOutpoint().getTxHash()) && input.getOutpoint().getIndex() == utxo.getOutpoint().getTxOutputN()) {
                    filteredUtxos.remove(utxo);
                }
            }
        }
        return filteredUtxos;
    }
}
