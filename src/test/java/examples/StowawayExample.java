package examples;

import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.cahoots.ManualCahootsMessage;
import com.samourai.wallet.cahoots.ManualCahootsService;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

public class StowawayExample {
    private static final NetworkParameters params = TestNet3Params.get();

    // TODO instanciate real wallets here!
    private static final CahootsWallet cahootsWalletSender = null;
    private static final CahootsWallet cahootsWalletCounterparty = null;

    public void Stowaway() throws Exception {

        // instanciate sender
        int senderAccount = 0;
        ManualCahootsService cahootsSender = new ManualCahootsService(params, cahootsWalletSender);

        // instanciate receiver
        int receiverAccount = 0; //TODO
        ManualCahootsService cahootsReceiver = new ManualCahootsService(params, cahootsWalletCounterparty);

        // STEP 0: sender
        long spendAmount = 5000;
        ManualCahootsMessage message0 = cahootsSender.newStowaway(senderAccount, spendAmount);

        // STEP 1: receiver
        ManualCahootsMessage message1 = cahootsReceiver.reply(receiverAccount, message0);

        // STEP 2: sender
        ManualCahootsMessage message2 = cahootsSender.reply(senderAccount, message1);

        // STEP 3: receiver
        ManualCahootsMessage message3 = cahootsReceiver.reply(receiverAccount, message2);

        // STEP 4: sender
        cahootsSender.reply(senderAccount, message3);

        // SUCCESS
    }
}
