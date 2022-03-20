package com.samourai.wallet.hd;

import com.samourai.wallet.bipWallet.BipWallet;

public class BipAddress {
    private HD_Address hdAddress;
    private BipWallet bipWallet;

    public BipAddress(HD_Address hdAddress, BipWallet bipWallet) {
        this.hdAddress = hdAddress;
        this.bipWallet = bipWallet;
    }

    public HD_Address getHdAddress() {
        return hdAddress;
    }

    public String getAddressString() {
        return bipWallet.getBipFormat().getAddressString(hdAddress);
    }

    public String getPathAddress() {
        return bipWallet.getDerivation().getPathAddress(hdAddress);
    }

    @Override
    public String toString() {
        return "BipAddress{" +
                "address=" + getAddressString() +
                ", path=" + getPathAddress() +
                '}';
    }
}
