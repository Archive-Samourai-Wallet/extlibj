package com.samourai.wallet.bipFormat;

import com.samourai.wallet.util.FormatsUtilGeneric;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BipFormatSupplierImpl implements BipFormatSupplier {
    private Map<String,BipFormat> bipFormats = new LinkedHashMap<>();

    public BipFormatSupplierImpl() {
        register(BIP_FORMAT.LEGACY);
        register(BIP_FORMAT.SEGWIT_COMPAT);
        register(BIP_FORMAT.SEGWIT_NATIVE);
    }

    public void register(BipFormat bipFormat) {
        bipFormats.put(bipFormat.getId(), bipFormat);
    }

    @Override
    public BipFormat findByAddress(String address, NetworkParameters params) {
        // TODO make it dynamic?
        if (FormatsUtilGeneric.getInstance().isValidBech32(address)) {
            return BIP_FORMAT.SEGWIT_NATIVE;
        } else if (Address.fromBase58(params, address).isP2SHAddress()) {
            return BIP_FORMAT.SEGWIT_COMPAT;
        }
        return BIP_FORMAT.LEGACY;
    }

    @Override
    public BipFormat findById(String bipFormatId) {
        return bipFormats.get(bipFormatId);
    }

    @Override
    public Collection<BipFormat> getList() {
        return bipFormats.values();
    }
}
