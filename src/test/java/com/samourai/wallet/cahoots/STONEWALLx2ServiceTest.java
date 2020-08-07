package com.samourai.wallet.cahoots;

import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.hd.java.HD_WalletFactoryJava;
import com.samourai.wallet.send.UTXO;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class STONEWALLx2ServiceTest {
    private HD_WalletFactoryJava hdWalletFactory = HD_WalletFactoryJava.getInstance();
    private NetworkParameters params = TestNet3Params.get();

    private STONEWALLx2Service counterpartyService;
    private STONEWALLx2Service senderService;

    @BeforeEach
    public void setUp() throws Exception {
        // counterparty
        HD_Wallet hdWallet1 = hdWalletFactory.getHD(44, "foo1".getBytes(), "test1", params);
        counterpartyService = computeService(hdWallet1);

        // sender
        HD_Wallet hdWallet2 = hdWalletFactory.getHD(44, "foo2".getBytes(), "test2", params);
        senderService = computeService(hdWallet2);
    }

    private STONEWALLx2Service computeService(HD_Wallet hdWallet) {
        STONEWALLx2Service service = new STONEWALLx2Service(hdWallet, params) {
            @Override
            ECKey getPrivKey(String address, int account) {
                return null;
            }

            @Override
            String getUnspentPath(String address) {
                return null;
            }

            @Override
            List<UTXO> getCahootsUTXO(int account) {
                return null;
            }

            @Override
            long getFeePerB() {
                return 0;
            }

            @Override
            int getHighestPostChangeIdx() {
                return 0;
            }
        };
        return service;
    }

    @Test
    public void STONEWALLx2() throws Exception {
        // counterparty
        long spendAmount = 1;
        String address = "";
        int account = 0;
        STONEWALLx2 payload_0 = counterpartyService.doSTONEWALLx2_0(spendAmount, address, account);

        // sender
        STONEWALLx2 payload_1 = senderService.doSTONEWALLx2_1(payload_0);

        // counterparty
        STONEWALLx2 payload_2 = counterpartyService.doSTONEWALLx2_2(payload_1);

        // sender
        STONEWALLx2 payload_3 = senderService.doSTONEWALLx2_3(payload_2);
    }
}
