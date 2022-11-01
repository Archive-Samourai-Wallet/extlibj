package examples;

import com.samourai.http.client.IHttpClient;
import com.samourai.http.client.JettyHttpClient;
import com.samourai.soroban.cahoots.*;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.xmanager.client.XManagerClient;

import java.util.Optional;

public class Stonewallx2Example {
    // TODO instanciate real wallets here!
    private static final CahootsWallet cahootsWalletSender = null; // new SimpleCahootsWallet(...)
    private static final CahootsWallet cahootsWalletCounterparty = null; // new SimpleCahootsWallet(...)

    public void Stonewallx2() throws Exception {
        // configure xManagerClient
        IHttpClient httpClient = new JettyHttpClient(10000, Optional.empty(), "test");
        XManagerClient xManagerClient = new XManagerClient(httpClient, true, false);

        // instanciate sender
        int senderAccount = 0;
        ManualCahootsService cahootsSender = new ManualCahootsService(cahootsWalletSender, xManagerClient);

        // instanciate counterparty
        int receiverAccount = 0;
        ManualCahootsService cahootsCounterparty = new ManualCahootsService(cahootsWalletCounterparty, xManagerClient);

        // STEP 0: sender
        long feePerB = 1;
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";
        String paynymDestination = null;
        boolean rbfOptin = false;
        Stonewallx2Context contextSender = Stonewallx2Context.newInitiator(senderAccount, feePerB, spendAmount, address, paynymDestination, rbfOptin);
        ManualCahootsMessage message0 = cahootsSender.initiate(contextSender);

        // STEP 1: counterparty
        StowawayContext contextReceiver = StowawayContext.newCounterparty(receiverAccount);
        ManualCahootsMessage message1 = (ManualCahootsMessage)cahootsCounterparty.reply(contextReceiver, message0);

        // STEP 2: sender
        ManualCahootsMessage message2 = (ManualCahootsMessage)cahootsSender.reply(contextSender, message1);

        // STEP 3: counterparty
        ManualCahootsMessage message3 = (ManualCahootsMessage)cahootsCounterparty.reply(contextReceiver, message2);

        // STEP 4: sender confirm TX_BROADCAST
        TxBroadcastInteraction txBroadcastInteraction = (TxBroadcastInteraction)cahootsSender.reply(contextSender, message3);
        ManualCahootsMessage message4 = (ManualCahootsMessage)txBroadcastInteraction.getReplyAccept();

        // SUCCESS
    }
}
