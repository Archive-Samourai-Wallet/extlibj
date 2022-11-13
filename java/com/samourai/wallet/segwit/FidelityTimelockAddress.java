package com.samourai.wallet.segwit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.math.BigInteger;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;

public class FidelityTimelockAddress extends TimelockAddress {

    private int timelockIndex = 0;

    public FidelityTimelockAddress(byte[] pubkey, NetworkParameters params, int index) throws Exception {
        super(pubkey, params, TYPE_P2WSH);
        this.timelockIndex = index;
        this.timelock = getTimelockAsUnixTime();
    }

    public FidelityTimelockAddress(ECKey ecKey, NetworkParameters params, int index) throws Exception {
        super(ecKey, params);
        this.DEFAULT_TO = TYPE_P2WSH;
        this.timelockIndex = index;
        this.timelock = getTimelockAsUnixTime();
    }

    private long getTimelockAsUnixTime() throws Exception {

        int year = 2020 + (timelockIndex / 12);
        int month = 1 + (timelockIndex % 12);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        String dateString = String.format("%4d", year);
        dateString += "-";
        dateString += String.format("%02d", month);
        dateString += "-";
        dateString += "01 00:00:00 UTC";
        Date date = dateFormat.parse(dateString);

        long unixTime = date.getTime() / 1000L;

        return unixTime;
    }

}
