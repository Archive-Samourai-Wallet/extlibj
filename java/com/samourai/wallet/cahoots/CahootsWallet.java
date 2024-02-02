package com.samourai.wallet.cahoots;

import com.samourai.wallet.bip47.rpc.BIP47Account;
import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.bipWallet.WalletSupplier;
import com.samourai.wallet.chain.ChainSupplier;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.send.provider.CahootsUtxoProvider;
import com.samourai.whirlpool.client.wallet.beans.SamouraiAccountIndex;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;

import java.util.List;

public class CahootsWallet {
    private WalletSupplier walletSupplier;
    private BipFormatSupplier bipFormatSupplier;
    private ChainSupplier chainSupplier;
    private CahootsUtxoProvider utxoProvider;

    private byte[] fingerprint;
    private BIP47Account bip47Account;

    public CahootsWallet(WalletSupplier walletSupplier, ChainSupplier chainSupplier, BipFormatSupplier bipFormatSupplier, CahootsUtxoProvider utxoProvider, HD_Wallet hdWallet, int account) {
        this.walletSupplier = walletSupplier;
        this.chainSupplier = chainSupplier;
        this.bipFormatSupplier = bipFormatSupplier;
        this.utxoProvider = utxoProvider;

        this.fingerprint = hdWallet.getFingerprint();
        this.bip47Account = new BIP47Wallet(hdWallet).getAccount(account);
    }

    public CahootsWallet(WalletSupplier walletSupplier, ChainSupplier chainSupplier, BipFormatSupplier bipFormatSupplier, CahootsUtxoProvider utxoProvider) {
        this(walletSupplier, chainSupplier, bipFormatSupplier, utxoProvider,
                walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84).getHdWallet(), 0);
    }

        private BipFormat likeTypedBipFormat(BipFormat bipFormat) {
        if (bipFormat == BIP_FORMAT.TAPROOT) {
            // like-typed output is not implemented for TAPROOT => handle TAPROOT mix output as SEGWIT_NATIVE
            return BIP_FORMAT.SEGWIT_NATIVE;
        }
        return bipFormat;
    }

    public BipWallet getReceiveWallet(int account, BipFormat bipFormat) throws Exception {
        bipFormat = likeTypedBipFormat(bipFormat);
        WhirlpoolAccount whirlpoolAccount = SamouraiAccountIndex.find(account);
        return walletSupplier.getWallet(whirlpoolAccount, bipFormat);
    }

    public BipAddress fetchAddressReceive(int account, boolean increment, BipFormat bipFormat) throws Exception {
        bipFormat = likeTypedBipFormat(bipFormat);
        return getReceiveWallet(account, bipFormat).getNextAddressReceive(bipFormat, increment);
    }

    public BipAddress fetchAddressChange(int account, boolean increment, BipFormat bipFormat) throws Exception {
        bipFormat = likeTypedBipFormat(bipFormat);
        return getReceiveWallet(account, bipFormat).getNextAddressChange(bipFormat, increment);
    }

    public BipFormatSupplier getBipFormatSupplier() {
        return bipFormatSupplier;
    }

    public ChainSupplier getChainSupplier() {
        return chainSupplier;
    }

    public byte[] getFingerprint() {
        return fingerprint;
    }

    public List<CahootsUtxo> getUtxosWpkhByAccount(int account) {
        return utxoProvider.getUtxosWpkhByAccount(account);
    }

    public BIP47Account getBip47Account() {
        return bip47Account;
    }
}
