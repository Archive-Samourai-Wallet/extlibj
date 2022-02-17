package com.samourai.wallet.bipFormat;

import com.samourai.wallet.segwit.bech32.Bech32Segwit;
import com.samourai.wallet.util.FormatsUtilGeneric;
import org.apache.commons.lang3.tuple.Pair;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BipFormatSupplierImpl implements BipFormatSupplier {
    private Map<String,BipFormat> bipFormats = new LinkedHashMap<>();

    public BipFormatSupplierImpl() {
        register(BIP_FORMAT.LEGACY);
        register(BIP_FORMAT.SEGWIT_COMPAT);
        register(BIP_FORMAT.SEGWIT_NATIVE);
        register(BIP_FORMAT.TAPROOT);
    }

    public void register(BipFormat bipFormat) {
        bipFormats.put(bipFormat.getId(), bipFormat);
    }

    @Override
    public BipFormat findByAddress(String address, NetworkParameters params) {
        // TODO make it dynamic?
        try {
            Pair<Byte, byte[]> bech32Segwit = Bech32Segwit.decode(params instanceof TestNet3Params ? "tb" : "bc", address);
            int witnessVer = bech32Segwit.getLeft().intValue();
            if(witnessVer == 0) {
                // P2WPKH, P2WSH
                return BIP_FORMAT.SEGWIT_NATIVE;
            } else if(witnessVer == 1) {
                // P2TR
                return BIP_FORMAT.TAPROOT;
            }
        } catch(NullPointerException ignored) {
        }

        if (Address.fromBase58(params, address).isP2SHAddress()) {
            // P2SH
            return BIP_FORMAT.SEGWIT_COMPAT;
        } else {
            // P2PKH
            return BIP_FORMAT.LEGACY;
        }
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
