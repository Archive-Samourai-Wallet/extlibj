package examples;

import com.samourai.wallet.cahoots.CahootsMessage;
import com.samourai.wallet.cahoots.CahootsService;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.cahoots.STONEWALLx2;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

public class Stonewallx2Example {
    private static final NetworkParameters params = TestNet3Params.get();

    // TODO instanciate real wallets here!
    private static final CahootsWallet cahootsWalletSender = null;
    private static final CahootsWallet cahootsWalletCounterparty = null;

    public void Stonewallx2() throws Exception {

        // instanciate sender
        long senderFeePerB = 1;
        int senderAccount = 0;
        CahootsService cahootsSender = new CahootsService(params, cahootsWalletSender, senderFeePerB, senderAccount);

        // instanciate counterparty
        long receiverFeePerB = 1;
        int receiverAccount = 0; //TODO
        CahootsService cahootsCounterparty = new CahootsService(params, cahootsWalletCounterparty, receiverFeePerB, receiverAccount);

        // STEP 0: sender
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";
        CahootsMessage message0 = cahootsSender.newStonewallx2(spendAmount, address);

        // STEP 1: counterparty
        CahootsMessage message1 = cahootsCounterparty.reply(message0);

        // STEP 2: sender
        CahootsMessage message2 = cahootsSender.reply(message1);

        // STEP 3: counterparty
        CahootsMessage message3 = cahootsCounterparty.reply(message2);

        // STEP 4: sender
        cahootsSender.reply(message3);

        // SUCCESS
    }
}
