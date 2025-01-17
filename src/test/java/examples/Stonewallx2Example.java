package examples;

import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.wallet.cahoots.TxBroadcastInteraction;
import com.samourai.wallet.cahoots.manual.ManualCahootsMessage;
import com.samourai.wallet.cahoots.manual.ManualCahootsService;
import com.samourai.wallet.cahoots.stonewallx2.Stonewallx2Context;
import com.samourai.wallet.util.ExtLibJConfig;

public class Stonewallx2Example {
    // TODO instanciate real wallets here!
    private static final CahootsWallet cahootsWalletSender = null; // new CahootsWallet(...)
    private static final CahootsWallet cahootsWalletCounterparty = null; // new CahootsWallet(...)

    public void Stonewallx2() throws Exception {
        // instanciate service
        ExtLibJConfig extLibJConfig = null; // TODO provide impl
        ManualCahootsService cahootsService = new ManualCahootsService(extLibJConfig);

        // instanciate sender
        int senderAccount = 0;

        // instanciate counterparty
        int receiverAccount = 0;

        // STEP 0: sender
        long feePerB = 1;
        long spendAmount = 5000;
        String address = "tb1q9m8cc0jkjlc9zwvea5a2365u6px3yu646vgez4";
        String paynymDestination = null;
        Stonewallx2Context contextSender = Stonewallx2Context.newInitiator(cahootsWalletSender, senderAccount, feePerB, spendAmount, address, paynymDestination);
        ManualCahootsMessage message0 = cahootsService.initiate(contextSender);

        // STEP 1: counterparty
        Stonewallx2Context contextReceiver = Stonewallx2Context.newCounterparty(cahootsWalletCounterparty, receiverAccount);
        ManualCahootsMessage message1 = (ManualCahootsMessage)cahootsService.reply(contextReceiver, message0);

        // STEP 2: sender
        ManualCahootsMessage message2 = (ManualCahootsMessage)cahootsService.reply(contextSender, message1);

        // STEP 3: counterparty
        ManualCahootsMessage message3 = (ManualCahootsMessage)cahootsService.reply(contextReceiver, message2);

        // STEP 4: sender confirm TX_BROADCAST
        TxBroadcastInteraction txBroadcastInteraction = (TxBroadcastInteraction)cahootsService.reply(contextSender, message3);
        ManualCahootsMessage message4 = (ManualCahootsMessage)txBroadcastInteraction.getReplyAccept();

        // SUCCESS
    }
}
