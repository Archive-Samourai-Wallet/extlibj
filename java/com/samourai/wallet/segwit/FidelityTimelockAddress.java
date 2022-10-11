package com.samourai.wallet.segwit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.math.BigInteger;

import com.samourai.wallet.segwit.bech32.Bech32Segwit;
import com.samourai.wallet.util.Util;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;

import org.bouncycastle.util.encoders.Hex;

import org.apache.commons.lang3.ArrayUtils;

public class FidelityTimelockAddress extends SegwitAddress {

    private long timelock = 0L;
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

    public String getFidelityBondTimelockAddressAsString()    {

        String address = null;

        try {
            address = Bech32Segwit.encode(params instanceof TestNet3Params ? "tb" : "bc", (byte)0x00, Util.sha256(fidelityBondTimelockRedeemScript().getProgram()));
        }
        catch(Exception e) {
            ;
        }

        return address;
    }

    public long getTimelock()  {

      return timelock;

    }

    public String getDefaultToAddressAsString()  {

      return getFidelityBondTimelockAddressAsString();

    }

    public Script fidelityBondTimelockOutputScript() throws NoSuchAlgorithmException    {

        byte[] hash = Util.sha256(fidelityBondTimelockRedeemScript().getProgram());
        byte[] buf = new byte[2 + hash.length];
        buf[0] = (byte)0x00;
        buf[1] = (byte)0x20;
        System.arraycopy(hash, 0, buf, 2, hash.length);

        return new Script(buf);
    }

    public Script fidelityBondTimelockRedeemScript()    {

        //
        // <timelock> OP_CHECKLOCKTIMEVERIFY OP_DROP <derived_key> OP_CHECKSIG
        //
        byte[] lock = getTimelockAsByteArray();
        byte[] locklen = new byte[1];
        locklen[0] = (byte)lock.length;
        byte[] pubkey = this.ecKey.getPubKey();
        byte[] buf = new byte[1 + lock.length + 3 + pubkey.length + 1];
        System.arraycopy(locklen, 0, buf, 0, locklen.length);
        System.arraycopy(lock, 0, buf, locklen.length, lock.length);
        buf[locklen.length + lock.length] = (byte)0xb1;
        buf[locklen.length + lock.length + 1] = (byte)0x75;
        buf[locklen.length + lock.length + 2] = (byte)0x21;
        System.arraycopy(pubkey, 0, buf, locklen.length + lock.length + 3, pubkey.length);
        buf[locklen.length + lock.length + 3 + pubkey.length] = (byte)0xac;

        return new Script(buf);
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

    private byte[] getTimelockAsByteArray() {

      BigInteger biTimelock = BigInteger.valueOf(timelock);
      byte[] lock = new byte[biTimelock.toByteArray().length];
      System.arraycopy(biTimelock.toByteArray(), 0, lock, 0, biTimelock.toByteArray().length);

      ArrayUtils.reverse(lock);

      return lock;
    }

}
