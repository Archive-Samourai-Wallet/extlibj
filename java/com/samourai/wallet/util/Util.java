package com.samourai.wallet.util;

import com.samourai.wallet.cahoots.psbt.PSBT;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Util  {

  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  public static String bytesToHex(byte[] b) {
      char[] hexChars = new char[b.length * 2];
      for (int j = 0; j < b.length; j++) {
          int v = b[j] & 0xFF;
          hexChars[j * 2] = HEX_ARRAY[v >>> 4];
          hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
      }
      return new String(hexChars);
  }

  public static byte[] hexToBytes(String s) {
      int len = s.length();
      byte[] buf = new byte[len / 2];
      for (int i = 0; i < len; i += 2) {
          buf[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
      }
      return buf;
  }

  public static byte[] bytesFromInt(int n) {
      return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(n).array();
  }

  public static byte[] bytesFromBigInteger(BigInteger n) {

      byte[] b = n.toByteArray();

      if(b.length == 32) {
          return b;
      }
      else if(b.length > 32) {
          return Arrays.copyOfRange(b, b.length - 32, b.length);
      }
      else {
          byte[] buf = new byte[32];
          System.arraycopy(b, 0, buf, buf.length - b.length, b.length);
          return buf;
      }
  }

  public static BigInteger bigIntFromBytes(byte[] b) {
      return new BigInteger(1, b);
  }

    public static byte[] xor(byte[] b0, byte[] b1)   {

        if(b0.length != b1.length)   {
            return  null;
        }

        byte[] ret = new byte[b0.length];
        int i = 0;
        for (byte b : b0)   {
            ret[i] = (byte)(b ^ b1[i]);
            i++;
        }

        return ret;
    }

    public static byte[] sha256(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(b);
    }

    public static byte[] getHMAC(byte[] b0, byte[] b1)  {

        Mac sha512_HMAC = null;
        byte[] mac_data = null;

        try {
            sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretkey = new SecretKeySpec(b0, "HmacSHA512");
            sha512_HMAC.init(secretkey);
            mac_data = sha512_HMAC.doFinal(b1);
        }
        catch(InvalidKeyException jse) {
            return null;
        }
        catch(NoSuchAlgorithmException nsae) {
            return null;
        }

        return mac_data;
    }

    public static <O> Collection<O> intersection(Collection<O> collection, Collection<O> otherCollection) {
        return collection.stream()
                .distinct()
                .filter(otherCollection::contains)
                .collect(Collectors.toSet());
    }

    public static String maskString(String value) {
        return maskString(value, 3);
    }

    private static String maskString(String value, int startEnd) {
        if (value == null) {
            return "null";
        }
        if (value.length() <= startEnd) {
            return value;
        }
        return value.substring(0, Math.min(startEnd, value.length()))
                + "..."
                + value.substring(Math.max(0, value.length() - startEnd), value.length());
    }

    public static long sumLong(Collection<Long> values) {
      return values.stream().mapToLong(v -> v).sum();
    }

    public static int sumInt(Collection<Integer> values) {
        return values.stream().mapToInt(v -> v).sum();
    }
}
