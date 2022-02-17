package com.samourai.wallet.bip340;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import com.samourai.wallet.segwit.bech32.*;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.UnsafeByteArrayOutputStream;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.math.ec.ECPoint;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.bouncycastle.util.encoders.Hex;

import javax.annotation.Nullable;

public class BIP340Util {

      public static Point getInternalPubkey(ECKey eckey) {
        ECPoint ecPoint = eckey.getPubKeyPoint();
        Point point = new Point(ecPoint.getAffineXCoord().toBigInteger(), ecPoint.getAffineYCoord().toBigInteger());
        Point iPoint = Point.liftX(point.getX().toByteArray());
        return iPoint;
      }

      public static String getP2TRAddress(NetworkParameters params, Point opoint) {
        if(Point.isSecp256k1(opoint.toBytes())) {
          String address = Bech32Segwit.encode(params instanceof TestNet3Params ? "tb" : "bc", (byte)0x01, BigIntegers.asUnsignedByteArray(opoint.getX()));
          return address;
        }
        else {
          return null;
        }
      }

      public static String getP2TRAddress(NetworkParameters params, ECKey eckey, boolean tweak) {
        Point iPoint = getInternalPubkey(eckey);
        Point oPoint = null;
        if(tweak) {
            oPoint = getTweakedPubKeyFromPoint(iPoint);
        } else {
            oPoint = iPoint;
        }
        if(Point.isSecp256k1(oPoint.toBytes())) {
          String address = Bech32Segwit.encode(params instanceof TestNet3Params ? "tb" : "bc", (byte)0x01, BigIntegers.asUnsignedByteArray(oPoint.getX()));
          return address;
        }
        else {
          return null;
        }
      }

    /**
     *
     * @param originalPrivKey The original private key.
     * @param hash For more complex Taproot functionality you would commit to a scripthash tree. For single-sig wallets this will almost always be null.
     * @return Returns a private key in bytes.
     * @throws IOException
     */
    public static ECKey getTweakedPrivKey(ECKey originalPrivKey, @Nullable byte[] hash) throws IOException {
        BigInteger privKey0 = originalPrivKey.getPrivKey();
        Point privPoint = Point.mul(Point.getG(), originalPrivKey.getPrivKey());
        BigInteger privKey;
        if(privPoint.hasEvenY()) {
            privKey = privKey0;
        } else {
            privKey = Point.getn().subtract(privKey0);
        }
        ByteArrayOutputStream bos = new UnsafeByteArrayOutputStream(32);
        byte[] tag = Sha256Hash.hash("TapTweak".getBytes());
        bos.write(tag);
        bos.write(tag);
        bos.write(privPoint.toBytes());
        if(hash != null) {
            bos.write(hash);
        }
        byte[] tweak = Sha256Hash.hash(bos.toByteArray());
        ECKey tweakKey = ECKey.fromPrivate(tweak);
        return ECKey.fromPrivate((privKey.add(tweakKey.getPrivKey())).mod(Point.getn()));
    }

    public static Point getTweakedPubKeyFromPoint(Point ipoint) {
        Point oPoint = null;
        try {
            byte[] taggedHash = Point.taggedHash("TapTweak", BigIntegers.asUnsignedByteArray(ipoint.getX()));
            oPoint = Point.add(Point.getG().mul(new BigInteger(1, taggedHash)), ipoint);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        return oPoint;
    }
}
