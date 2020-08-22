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
        long senderFeePerB = 1;
        int senderAccount = 0;
        CahootsService cahootsSender = new CahootsService(params, cahootsWalletSender, senderFeePerB, senderAccount);

        // instanciate receiver
        long receiverFeePerB = 1;
        int receiverAccount = 0; //TODO
        CahootsService cahootsReceiver = new CahootsService(params, cahootsWalletCounterparty, receiverFeePerB, receiverAccount);

        // STEP 0: sender
        long spendAmount = 5000;
        CahootsMessage message = cahootsSender.newStowaway(spendAmount);

        // STEP 1-4
        while(message != null) {
            message = cahootsSender.reply(message);
        }

        // SUCCESS
    }
}
