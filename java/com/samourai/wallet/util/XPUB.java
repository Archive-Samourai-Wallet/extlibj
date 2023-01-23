package com.samourai.wallet.util;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;

public class XPUB {

    private static final int CHAIN_LEN = 32;
    private static final int PUBKEY_LEN = 33;

    public static final int MAGIC_XPUB = 0x0488B21E;
    public static final int MAGIC_TPUB = 0x043587CF;
    public static final int MAGIC_YPUB = 0x049D7CB2;
    public static final int MAGIC_UPUB = 0x044A5262;
    public static final int MAGIC_ZPUB = 0x04B24746;
    public static final int MAGIC_VPUB = 0x045F1CF6;

    private String strXPUB = null;

    private byte[] chain = null;
    private byte[] pub = null;
    private byte depth = 0x00;
    private int fingerprint = -1;
    private int child = -1;

    private XPUB()  { ; }

    public XPUB(String xpub)   {

        strXPUB = xpub;
        chain = new byte[CHAIN_LEN];
        pub = new byte[PUBKEY_LEN];

    }

    public void decode() throws AddressFormatException {
    
        byte[] xpubBytes = Base58.decodeChecked(strXPUB);

        ByteBuffer bb = ByteBuffer.wrap(xpubBytes);
        int ver = bb.getInt();
        if(ver != MAGIC_XPUB && ver != MAGIC_TPUB && ver != MAGIC_YPUB && ver != MAGIC_UPUB && ver != MAGIC_ZPUB && ver != MAGIC_VPUB)   {
            throw new AddressFormatException("invalid xpub version");
        }

        // depth:
        depth = bb.get();
        // parent fingerprint:
        fingerprint = bb.getInt();
        // child no.
        child = bb.getInt();
        // chain
        bb.get(chain);
        // 
        bb.get(pub);

    }

    public byte[] getPubkey() {
	    return pub;
    }

    public byte[] getChain() {
	    return chain;
    }

    public byte getDepth() {
	    return depth;
    }

    public int getFingerprint() {
	    return fingerprint;
    }

    public int getChild() {
	    return child;
    }

}
