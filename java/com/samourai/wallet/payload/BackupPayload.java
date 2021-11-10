package com.samourai.wallet.payload;

import com.fasterxml.jackson.databind.JsonNode;
import com.samourai.wallet.api.pairing.PairingPayload;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.hd.HD_WalletFactoryGeneric;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.wallet.util.JSONUtils;
import org.bitcoinj.core.NetworkParameters;

public class BackupPayload {
    private JsonNode wallet;
    private JsonNode meta;

    public BackupPayload() {
        this.wallet = null;
        this.meta = null;
    }

    public static BackupPayload parse(String json) throws Exception {
        return JSONUtils.getInstance().getObjectMapper().readValue(json, BackupPayload.class);
    }

    public boolean isWalletTestnet() {
        if (wallet == null) {
            return false;
        }
        if (wallet.get("testnet") == null) {
            return false;
        }
        return wallet.get("testnet").booleanValue();
    }

    public NetworkParameters computeNetworkParameters() {
        return FormatsUtilGeneric.getInstance().getNetworkParams(isWalletTestnet());
    }

    public String getWalletSeed() {
        if (wallet == null) return null;
        return wallet.get("seed").textValue();
    }

    public String getWalletPassphrase() {
        if (wallet == null) return null;
        return wallet.get("passphrase").textValue();
    }

    public HD_Wallet computeHdWallet() throws Exception {
        byte[] seed = org.apache.commons.codec.binary.Hex.decodeHex(getWalletSeed().toCharArray());
        String passphrase = getWalletPassphrase();
        NetworkParameters params = computeNetworkParameters();
        return HD_WalletFactoryGeneric.getInstance().getHD(44, seed, passphrase, params);
    }

    public PairingPayload.PairingDojo computePairingDojo() {
        if (wallet == null) return null;
        if (wallet.get("dojo") == null) return null;
        JsonNode dojoPairingNode = wallet.get("dojo").get("pairing");
        if (dojoPairingNode == null) return null;
        String url = dojoPairingNode.get("url").textValue();
        String apiKey = dojoPairingNode.get("apikey").textValue();
        if (url == null || apiKey == null) return null;

        PairingPayload.PairingDojo pairingDojo = new PairingPayload.PairingDojo(url, apiKey);
        return pairingDojo;
    }

    public JsonNode getWallet() {
        return wallet;
    }

    public void setWallet(JsonNode wallet) {
        this.wallet = wallet;
    }

    public JsonNode getMeta() {
        return meta;
    }

    public void setMeta(JsonNode meta) {
        this.meta = meta;
    }
}
