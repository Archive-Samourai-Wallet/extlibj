package com.samourai.wallet;

import org.bitcoinj.core.Coin;

import java.math.BigInteger;

public class SamouraiWalletConst {
    public static final BigInteger bDust = BigInteger.valueOf(Coin.parseCoin("0.00000546").longValue());    // https://github.com/bitcoin/bitcoin/pull/2760

}
