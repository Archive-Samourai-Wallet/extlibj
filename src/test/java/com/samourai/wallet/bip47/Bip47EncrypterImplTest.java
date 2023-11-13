package com.samourai.wallet.bip47;

import com.samourai.soroban.client.RpcWallet;
import com.samourai.soroban.client.RpcWalletImpl;
import com.samourai.wallet.bip47.rpc.Bip47EncrypterImpl;
import com.samourai.wallet.test.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Bip47EncrypterImplTest extends AbstractTest {
    private Bip47EncrypterImpl paynymEncrypterInitiator;
    private Bip47EncrypterImpl paynymEncrypterCounterparty;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        RpcWallet rpcWalletInitiator = new RpcWalletImpl(bip47WalletInitiator, cryptoUtil, bip47Util);
        paynymEncrypterInitiator = (Bip47EncrypterImpl) rpcWalletInitiator.getBip47Encrypter();

        RpcWallet rpcWalletCounterparty = new RpcWalletImpl(bip47WalletCounterparty, cryptoUtil, bip47Util);
        paynymEncrypterCounterparty = (Bip47EncrypterImpl) rpcWalletCounterparty.getBip47Encrypter();
    }

    @Test
    public void getNotificationAddress() {
        Assertions.assertEquals("mwkeyEBt55Jvg3TbqLKuL4BZ2SJd2VpZTM", bip47Util.getNotificationAddress(bip47Wallet).getAddressString());
        Assertions.assertEquals("mwkeyEBt55Jvg3TbqLKuL4BZ2SJd2VpZTM", bip47Util.getNotificationAddress(bip47Wallet, 0).getAddressString());
        Assertions.assertEquals("mn1GbUVNJ7NbwdNKocBYW2ZtWXbfbdQrcH", bip47Util.getNotificationAddress(bip47Wallet, 1).getAddressString());
    }

    @Test
    public void getPaymentCode() {
        String pCode = paynymEncrypterInitiator.getPaymentCode().toString();
        Assertions.assertEquals("PM8TJXp19gCE6hQzqRi719FGJzF6AreRwvoQKLRnQ7dpgaakakFns22jHUqhtPQWmfevPQRCyfFbdDrKvrfw9oZv5PjaCerQMa3BKkPyUf9yN1CDR3w6", pCode);
        Assertions.assertEquals(bip47Util.getPaymentCode(bip47Wallet).toString(), pCode);
    }

    @Test
    public void getPaymentAddress() throws Exception {
        String receiveAddress = paynymEncrypterInitiator.getSharedPaymentAddress(paymentCodeCounterparty)
                .getSegwitAddressReceive().getBech32AsString();
        Assertions.assertEquals("tb1q4udyravjfu8yx2hdswvx0jvc5j7zhvulemg7jn", receiveAddress);
        Assertions.assertEquals(bip47Util.getReceiveAddress(bip47WalletInitiator, paymentCodeCounterparty, 0, params).getBech32AsString(), receiveAddress);

        String sendAddress = paynymEncrypterCounterparty.getSharedPaymentAddress(paymentCodeInitiator)
                .getSegwitAddressSend().getBech32AsString();
        Assertions.assertEquals("tb1q4udyravjfu8yx2hdswvx0jvc5j7zhvulemg7jn", sendAddress);
        Assertions.assertEquals(bip47Util.getSendAddress(bip47WalletCounterparty, paymentCodeInitiator, 0, params).getBech32AsString(), sendAddress);
    }

    @Test
    public void sign() throws Exception {
        String message = "hello Soroban";
        String signature = paynymEncrypterInitiator.sign(message);
        Assertions.assertEquals("IP2vGNSVHJNu1MjUKM7NVajc0omP1OQvX/8i3GgEX6oufI/E+q5sR4ouD6B55BznUE1TSxmnlGlzzB5WnXJAKH4=", signature);
    }

    @Test
    public void verifySignature_success() throws Exception {
        String message = "hello Soroban";
        String signature = "IP2vGNSVHJNu1MjUKM7NVajc0omP1OQvX/8i3GgEX6oufI/E+q5sR4ouD6B55BznUE1TSxmnlGlzzB5WnXJAKH4=";
        Assertions.assertTrue(paynymEncrypterCounterparty.verifySignature(message, signature, paymentCodeInitiator));

        Assertions.assertFalse(paynymEncrypterCounterparty.verifySignature(message+"altered", signature, paymentCodeInitiator));
        Assertions.assertFalse(paynymEncrypterCounterparty.verifySignature(message, signature+"altered", paymentCodeInitiator));
        Assertions.assertFalse(paynymEncrypterCounterparty.verifySignature(message, signature, paymentCodeCounterparty));
    }
}
