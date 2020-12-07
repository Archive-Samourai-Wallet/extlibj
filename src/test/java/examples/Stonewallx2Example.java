package examples;

import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.ManualCahootsMessage;
import com.samourai.soroban.cahoots.ManualCahootsService;
import com.samourai.soroban.cahoots.TxBroadcastInteraction;
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
        ManualCahootsService cahootsSender = new ManualCahootsService(cahootsWalletSender);

        // instanciate counterparty
        int receiverAccount = 0;
        ManualCahootsService cahootsCounterparty = new ManualCahootsService(cahootsWalletCounterparty);

        // STEP 0: sender
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";
        CahootsContext contextSender = CahootsContext.newInitiatorStonewallx2(spendAmount, address);
        ManualCahootsMessage message0 = cahootsSender.initiate(senderAccount, contextSender);

        // STEP 1: counterparty
        CahootsContext contextReceiver = CahootsContext.newCounterpartyStonewallx2();
        ManualCahootsMessage message1 = (ManualCahootsMessage)cahootsCounterparty.reply(receiverAccount, contextReceiver, message0);

        // STEP 2: sender
        ManualCahootsMessage message2 = (ManualCahootsMessage)cahootsSender.reply(senderAccount, contextSender, message1);

        // STEP 3: counterparty
        ManualCahootsMessage message3 = (ManualCahootsMessage)cahootsCounterparty.reply(receiverAccount, contextReceiver, message2);

        // STEP 4: sender confirm TX_BROADCAST
        TxBroadcastInteraction txBroadcastInteraction = (TxBroadcastInteraction)cahootsSender.reply(senderAccount, contextSender, message3);
        ManualCahootsMessage message4 = (ManualCahootsMessage)txBroadcastInteraction.getReplyAccept();

        // SUCCESS
    }
}
