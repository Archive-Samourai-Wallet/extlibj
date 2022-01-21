package com.samourai.wallet.bip340;

import java.math.BigInteger;

import com.samourai.wallet.segwit.bech32.*;

import org.apache.commons.lang3.tuple.Pair;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.math.ec.ECPoint;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

public class BIP340Util {

      public static Point getInternalPubkey(ECKey eckey) {
        ECPoint ecPoint = eckey.getPubKeyPoint();
        Point point = new Point(ecPoint.getAffineXCoord().toBigInteger(), ecPoint.getAffineYCoord().toBigInteger());
        Point iPoint = Point.liftX(point.getX().toByteArray());
        return iPoint;
      }

      public static Point getOutputPubkey(Point ipoint) {
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

      public static String getP2TRAdress(NetworkParameters params, Point opoint) {
        String address = Bech32Segwit.encode(params instanceof TestNet3Params ? "tb" : "bc", (byte)0x01, BigIntegers.asUnsignedByteArray(opoint.getX()));
        return address;
      }

}
