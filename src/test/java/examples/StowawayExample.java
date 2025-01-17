package examples;

import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.cahoots.manual.ManualCahootsMessage;
import com.samourai.wallet.cahoots.manual.ManualCahootsService;
import com.samourai.wallet.cahoots.stowaway.StowawayContext;
import com.samourai.wallet.sorobanClient.SorobanInteraction;
import com.samourai.wallet.util.ExtLibJConfig;

public class StowawayExample {
    // instanciate real wallets here!
    private static final CahootsWallet cahootsWalletSender = null;
    private static final CahootsWallet cahootsWalletCounterparty = null;

    public void Stowaway() throws Exception {
        // instanciate service
        ExtLibJConfig extLibJConfig = null; // TODO provide impl
        ManualCahootsService cahootsService = new ManualCahootsService(extLibJConfig);

        // STEP 0: sender
        int senderAccount = 0;
        long feePerB = 1;
        long spendAmount = 5000;
        StowawayContext contextSender = StowawayContext.newInitiator(cahootsWalletSender, senderAccount, feePerB, spendAmount);
        ManualCahootsMessage message0 = cahootsService.initiate(contextSender);

        // STEP 1: receiver
        int receiverAccount = 0;
        StowawayContext contextReceiver = StowawayContext.newCounterparty(cahootsWalletCounterparty, receiverAccount);
        ManualCahootsMessage message1 = (ManualCahootsMessage)cahootsService.reply(contextReceiver, message0);

        // STEP 2: sender
        ManualCahootsMessage message2 = (ManualCahootsMessage)cahootsService.reply(contextSender, message1);

        // STEP 3: receiver
        ManualCahootsMessage message3 = (ManualCahootsMessage)cahootsService.reply(contextReceiver, message2);

        // STEP 4: sender
        SorobanInteraction confirmTx = (SorobanInteraction)cahootsService.reply(contextSender, message3);
        ManualCahootsMessage message4 = (ManualCahootsMessage)confirmTx.getReplyAccept();

        // SUCCESS
    }
}
