package com.samourai.wallet.cahoots;

import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.bipWallet.BipWallet;
import com.samourai.wallet.bipWallet.WalletSupplier;
import com.samourai.wallet.hd.BIP_WALLET;
import com.samourai.wallet.hd.BipAddress;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import com.samourai.wallet.whirlpool.WhirlpoolConst;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.bitcoinj.core.NetworkParameters;
import org.bouncycastle.util.encoders.Hex;

import java.util.LinkedList;
import java.util.List;

public abstract class CahootsWallet {
    private static final Bech32UtilGeneric bech32Util = Bech32UtilGeneric.getInstance();

    private WalletSupplier walletSupplier;
    private BipFormatSupplier bipFormatSupplier;
    private NetworkParameters params;

    public CahootsWallet(WalletSupplier walletSupplier, BipFormatSupplier bipFormatSupplier, NetworkParameters params) {
        this.walletSupplier = walletSupplier;
        this.bipFormatSupplier = bipFormatSupplier;
        this.params = params;
    }

    public abstract long fetchFeePerB();

    public BipWallet getReceiveWallet(int account, BipFormat bipFormat) throws Exception {
        if (account == WhirlpoolConst.WHIRLPOOL_POSTMIX_ACCOUNT) {
            // force change chain / BIP84
            return walletSupplier.getWallet(BIP_WALLET.POSTMIX_BIP84);
        }
        if (account == 0) {
            // like bipFormat
            return walletSupplier.getWallet(WhirlpoolAccount.DEPOSIT, bipFormat);
        }
        else {
            throw new Exception("Invalid account: "+account);
        }
    }

    public BipAddress fetchAddressReceive(int account, boolean increment, BipFormat bipFormat) throws Exception {
        return getReceiveWallet(account, bipFormat).getNextAddress(increment);
    }

    public BipAddress fetchAddressChange(int account, boolean increment) throws Exception {
        if (account == 0) {
            return walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84).getNextChangeAddress(increment);
        }
        else if (account == WhirlpoolConst.WHIRLPOOL_POSTMIX_ACCOUNT) {
            return walletSupplier.getWallet(BIP_WALLET.POSTMIX_BIP84).getNextChangeAddress(increment);
        }
        else {
            throw new Exception("Invalid account: "+account);
        }
    }

    protected abstract List<CahootsUtxo> fetchUtxos(int account);

    public BipFormatSupplier getBipFormatSupplier() {
        return bipFormatSupplier;
    }

    public NetworkParameters getParams() {
        return params;
    }

    public byte[] getFingerprint() {
        return walletSupplier.getWallet(BIP_WALLET.DEPOSIT_BIP84).getHdWallet().getFingerprint();
    }

    public List<CahootsUtxo> getUtxosWpkhByAccount(int account) {
        return filterUtxosWpkh(fetchUtxos(account));
    }

    protected static List<CahootsUtxo> filterUtxosWpkh(List<CahootsUtxo> utxos) {
        List<CahootsUtxo> filteredUtxos = new LinkedList<CahootsUtxo>();
        for(CahootsUtxo utxo : utxos)   {
            // filter wpkh
            String script = Hex.toHexString(utxo.getOutpoint().getScriptBytes());
            if (bech32Util.isP2WPKHScript(script)) {
                filteredUtxos.add(utxo);
            }
        }
        return filteredUtxos;
    }
}
