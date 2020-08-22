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
        CahootsMessage message = cahootsSender.newStonewallx2(spendAmount, address);

        // STEP 1-4
        while(message != null) {
            message = cahootsCounterparty.reply(message);
        }

        // SUCCESS
    }
}
