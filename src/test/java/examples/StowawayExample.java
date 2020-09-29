package examples;

import com.samourai.wallet.soroban.cahoots.CahootsContext;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.soroban.cahoots.ManualCahootsMessage;
import com.samourai.wallet.soroban.cahoots.ManualCahootsService;
import com.samourai.wallet.soroban.client.SorobanInteraction;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

public class StowawayExample {
    private static final NetworkParameters params = TestNet3Params.get();

    // instanciate real wallets here!
    private static final CahootsWallet cahootsWalletSender = null;
    private static final CahootsWallet cahootsWalletCounterparty = null;

    public void Stowaway() throws Exception {

        // instanciate sender
        int senderAccount = 0;
        ManualCahootsService cahootsSender = new ManualCahootsService(cahootsWalletSender);

        // instanciate receiver
        int receiverAccount = 0;
        ManualCahootsService cahootsReceiver = new ManualCahootsService(cahootsWalletCounterparty);

        // STEP 0: sender
        long spendAmount = 5000;
        CahootsContext contextSender = CahootsContext.newInitiatorStowaway(spendAmount);
        ManualCahootsMessage message0 = cahootsSender.initiate(senderAccount, contextSender);

        // STEP 1: receiver
        CahootsContext contextReceiver = CahootsContext.newCounterpartyStowaway();
        ManualCahootsMessage message1 = (ManualCahootsMessage)cahootsReceiver.reply(receiverAccount, contextReceiver, message0);

        // STEP 2: sender
        ManualCahootsMessage message2 = (ManualCahootsMessage)cahootsSender.reply(senderAccount, contextSender, message1);

        // STEP 3: receiver
        ManualCahootsMessage message3 = (ManualCahootsMessage)cahootsReceiver.reply(receiverAccount, contextReceiver, message2);

        // STEP 4: sender
        SorobanInteraction confirmTx = (SorobanInteraction)cahootsSender.reply(senderAccount, contextSender, message3);
        ManualCahootsMessage message4 = (ManualCahootsMessage)confirmTx.accept();

        // SUCCESS
    }
}
