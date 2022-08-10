package examples;

import com.samourai.http.client.IHttpClient;
import com.samourai.http.client.JettyHttpClient;
import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.ManualCahootsMessage;
import com.samourai.soroban.cahoots.ManualCahootsService;
import com.samourai.soroban.client.SorobanInteraction;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.xmanager.client.XManagerClient;

import java.util.Optional;

public class StowawayExample {
    // instanciate real wallets here!
    private static final CahootsWallet cahootsWalletSender = null;
    private static final CahootsWallet cahootsWalletCounterparty = null;

    public void Stowaway() throws Exception {
        // configure xManagerClient
        IHttpClient httpClient = new JettyHttpClient(10000, Optional.empty(), "test");
        XManagerClient xManagerClient = new XManagerClient(httpClient, true, false);

        // instanciate sender
        int senderAccount = 0;
        ManualCahootsService cahootsSender = new ManualCahootsService(cahootsWalletSender, xManagerClient);

        // instanciate receiver
        int receiverAccount = 0;
        ManualCahootsService cahootsReceiver = new ManualCahootsService(cahootsWalletCounterparty, xManagerClient);

        // STEP 0: sender
        long feePerB = 1;
        long spendAmount = 5000;
        CahootsContext contextSender = CahootsContext.newInitiatorStowaway(senderAccount, feePerB, spendAmount);
        ManualCahootsMessage message0 = cahootsSender.initiate(contextSender);

        // STEP 1: receiver
        CahootsContext contextReceiver = CahootsContext.newCounterpartyStowaway(receiverAccount);
        ManualCahootsMessage message1 = (ManualCahootsMessage)cahootsReceiver.reply(contextReceiver, message0);

        // STEP 2: sender
        ManualCahootsMessage message2 = (ManualCahootsMessage)cahootsSender.reply(contextSender, message1);

        // STEP 3: receiver
        ManualCahootsMessage message3 = (ManualCahootsMessage)cahootsReceiver.reply(contextReceiver, message2);

        // STEP 4: sender
        SorobanInteraction confirmTx = (SorobanInteraction)cahootsSender.reply(contextSender, message3);
        ManualCahootsMessage message4 = (ManualCahootsMessage)confirmTx.getReplyAccept();

        // SUCCESS
    }
}
