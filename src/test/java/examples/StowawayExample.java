package examples;

import com.samourai.wallet.cahoots.CahootsMessage;
import com.samourai.wallet.cahoots.CahootsService;
import com.samourai.wallet.cahoots.CahootsWallet;
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
        CahootsService cahootsSender = new CahootsService(params, cahootsWalletSender);

        // instanciate receiver
        int receiverAccount = 0; //TODO
        CahootsService cahootsReceiver = new CahootsService(params, cahootsWalletCounterparty);

        // STEP 0: sender
        long spendAmount = 5000;
        CahootsMessage message0 = cahootsSender.newStowaway(senderAccount, spendAmount);

        // STEP 1: receiver
        CahootsMessage message1 = cahootsReceiver.reply(receiverAccount, message0);

        // STEP 2: sender
        CahootsMessage message2 = cahootsSender.reply(senderAccount, message1);

        // STEP 3: receiver
        CahootsMessage message3 = cahootsReceiver.reply(receiverAccount, message2);

        // STEP 4: sender
        cahootsSender.reply(senderAccount, message3);

        // SUCCESS
    }
}
