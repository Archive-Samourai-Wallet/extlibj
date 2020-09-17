package examples;

import com.samourai.wallet.cahoots.*;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

public class Stonewallx2Example {
    private static final NetworkParameters params = TestNet3Params.get();

    // TODO instanciate real wallets here!
    private static final CahootsWallet cahootsWalletSender = null; // new SimpleCahootsWallet(...)
    private static final CahootsWallet cahootsWalletCounterparty = null; // new SimpleCahootsWallet(...)

    public void Stonewallx2() throws Exception {

        // instanciate sender
        int senderAccount = 0;
        ManualCahootsService cahootsSender = new ManualCahootsService(params, cahootsWalletSender);

        // instanciate counterparty
        int receiverAccount = 0;
        ManualCahootsService cahootsCounterparty = new ManualCahootsService(params, cahootsWalletCounterparty);

        // STEP 0: sender
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";
        ManualCahootsMessage message0 = cahootsSender.newStonewallx2(senderAccount, spendAmount, address);

        // STEP 1: counterparty
        ManualCahootsMessage message1 = cahootsCounterparty.reply(receiverAccount, message0);

        // STEP 2: sender
        ManualCahootsMessage message2 = cahootsSender.reply(senderAccount, message1);

        // STEP 3: counterparty
        ManualCahootsMessage message3 = cahootsCounterparty.reply(receiverAccount, message2);

        // STEP 4: sender
        cahootsSender.reply(senderAccount, message3);

        // SUCCESS
    }
}
