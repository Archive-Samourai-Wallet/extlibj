package examples;

import com.samourai.http.client.IHttpClient;
import com.samourai.http.client.JettyHttpClient;
import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.ManualCahootsMessage;
import com.samourai.soroban.cahoots.ManualCahootsService;
import com.samourai.soroban.client.SorobanInteraction;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.cahoots.CahootsWallet;
import com.samourai.xmanager.client.XManagerClient;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

import java.util.Optional;

public class StowawayExample {
    // instanciate real wallets here!
    private static final CahootsWallet cahootsWalletSender = null;
    private static final CahootsWallet cahootsWalletCounterparty = null;

    public void Stowaway() throws Exception {

        // configure xManagerClient
        IHttpClient httpClient = new JettyHttpClient(10000, Optional.empty(), "test");
        XManagerClient xManagerClient = new XManagerClient(httpClient, true, false);

        // instanciate service
        BipFormatSupplier bipFormatSupplier = BIP_FORMAT.PROVIDER;
        NetworkParameters params = TestNet3Params.get();
        ManualCahootsService cahootsService = new ManualCahootsService(bipFormatSupplier, params);

        // STEP 0: sender
        int senderAccount = 0;
        long feePerB = 1;
        long spendAmount = 5000;
        CahootsContext contextSender = CahootsContext.newInitiatorStowaway(cahootsWalletSender, senderAccount, feePerB, spendAmount);
        ManualCahootsMessage message0 = cahootsService.initiate(contextSender);

        // STEP 1: receiver
        int receiverAccount = 0;
        CahootsContext contextReceiver = CahootsContext.newCounterpartyStowaway(cahootsWalletCounterparty, receiverAccount);
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
