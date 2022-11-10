package examples;

import com.samourai.http.client.IHttpClient;
import com.samourai.http.client.JettyHttpClient;
import com.samourai.soroban.cahoots.*;
import com.samourai.wallet.api.backend.beans.WalletResponse;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.chain.ChainSupplier;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

import java.util.Optional;

public class Stonewallx2Example {
    // TODO instanciate real wallets here!
    private static final CahootsWallet cahootsWalletSender = null; // new CahootsWallet(...)
    private static final CahootsWallet cahootsWalletCounterparty = null; // new CahootsWallet(...)

    public void Stonewallx2() throws Exception {
        // configure xManagerClient
        IHttpClient httpClient = new JettyHttpClient(10000, Optional.empty(), "test");

        // instanciate service
        BipFormatSupplier bipFormatSupplier = BIP_FORMAT.PROVIDER;
        ChainSupplier mockChainSupplier = () -> {
            WalletResponse.InfoBlock infoBlock = new WalletResponse.InfoBlock();
            infoBlock.height = 1234;
            return infoBlock;
        };
        NetworkParameters params = TestNet3Params.get();
        ManualCahootsService cahootsService = new ManualCahootsService(bipFormatSupplier, mockChainSupplier, params);

        // instanciate sender
        int senderAccount = 0;

        // instanciate counterparty
        int receiverAccount = 0;

        // STEP 0: sender
        long feePerB = 1;
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";
        String paynymDestination = null;
        Stonewallx2Context contextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, senderAccount, feePerB, spendAmount, address, paynymDestination);
        ManualCahootsMessage message0 = cahootsService.initiate(contextSender);

        // STEP 1: counterparty
        StowawayContext contextReceiver = StowawayContext.newCounterparty(cahootsWalletCounterparty, receiverAccount);
        ManualCahootsMessage message1 = (ManualCahootsMessage)cahootsService.reply(contextReceiver, message0);

        // STEP 2: sender
        ManualCahootsMessage message2 = (ManualCahootsMessage)cahootsService.reply(contextSender, message1);

        // STEP 3: counterparty
        ManualCahootsMessage message3 = (ManualCahootsMessage)cahootsService.reply(contextReceiver, message2);

        // STEP 4: sender confirm TX_BROADCAST
        TxBroadcastInteraction txBroadcastInteraction = (TxBroadcastInteraction)cahootsService.reply(contextSender, message3);
        ManualCahootsMessage message4 = (ManualCahootsMessage)txBroadcastInteraction.getReplyAccept();

        // SUCCESS
    }
}
