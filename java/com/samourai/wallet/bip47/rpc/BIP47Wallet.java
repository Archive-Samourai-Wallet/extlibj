package com.samourai.wallet.bip47.rpc;

import com.samourai.wallet.hd.HD_Wallet;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

/**
 *
 * BIP47Wallet.java : BIP47 wallet
 *
 */
public class BIP47Wallet extends HD_Wallet {

    private BIP47Account mAccount = null;

    /**
     * Constructor for wallet.
     *
     * @param int purpose
     * @param MnemonicCode mc mnemonic code object
     * @param NetworkParameters params
     * @param byte[] seed seed for this wallet
     * @param String passphrase optional BIP39 passphrase
     *
     */
    public BIP47Wallet(int purpose, MnemonicCode mc, NetworkParameters params, byte[] seed, String passphrase) throws MnemonicException.MnemonicLengthException {

        super(purpose, mc, params, seed, passphrase);

        mAccount = new BIP47Account(params, mRoot, 0);

    }

    /**
     * Constructor for wallet.
     * @param hdWallet
     */
    public BIP47Wallet(HD_Wallet hdWallet) {
        this(47, hdWallet);
    }

    /**
     * Constructor for wallet.
     *
     * @param int purpose
     * @param HD_Wallet hdWallet to copy from
     *
     */
    public BIP47Wallet(int purpose, HD_Wallet hdWallet) {

        super(purpose, hdWallet);

        mAccount = new BIP47Account(mParams, mRoot, 0);

    }

    /**
     * Return account for submitted account id.
     *
     * @param int accountId
     *
     * @return Account
     *
     */
    public BIP47Account getAccount(int accountId) {
        return mAccount;
    }

}
