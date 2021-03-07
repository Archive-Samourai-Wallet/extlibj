package com.samourai.wallet.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormatsUtilGenericTest {
  private static final FormatsUtilGeneric formatsUtil = FormatsUtilGeneric.getInstance();

  private static final String VPUB_1 =
          "vpub5SLqN2bLY4WeYBwMrtdanr5SfhRC7AyW1aEwbtVbt7t9y6kgBCS6ajVA4LL7Jy2iojpH1hjaiTMp5h4y9dG2dC64bAk9ZwuFCX6AvxFddaa";
  private static final String VPUB_2 =
          "vpub5b14oTd3mpWGzbxkqgaESn4Pq1MkbLbzvWZju8Y6LiqsN9JXX7ZzvdCp1qDDxLqeHGr6BUssz2yFmUDm5Fp9jTdz4madyxK6mwgsCvYdK5S";
  private static final String XPUB_1 =
          "xpub6C8aSUjB7fwH6CSpS5AjRh1sPwfmrZKNNrfye5rkijhFpSfiKeSNT2CpVLuDzQiipdYAmmyi4eLXritVhYjfBfeEWJPXUrUEEHrcgnEH7wX";
  private static final String XPUB_2 =
          "xpub6DUQ2PuGdPGVK74fqMpFw7UxQa2wLcv8JcWEV7mjNgiuiv4NjgsxukpDfd6xaeuU87oEGx16k3w1XhCs4mmK8GybS6n9W5hvAvCtyxB9nLV";


  @Test
  public void isValidXpubOrZpub() throws Exception {
    Assertions.assertTrue(formatsUtil.isValidXpub(VPUB_1));
    Assertions.assertTrue(formatsUtil.isValidXpub(VPUB_2));
    Assertions.assertTrue(formatsUtil.isValidXpub(XPUB_1));
    Assertions.assertTrue(formatsUtil.isValidXpub(XPUB_2));

    Assertions.assertFalse(formatsUtil.isValidXpub("xpubfoo"));
    Assertions.assertFalse(formatsUtil.isValidXpub("vpubfoo"));
    Assertions.assertFalse(formatsUtil.isValidXpub(XPUB_1+"foo"));
    Assertions.assertFalse(formatsUtil.isValidXpub(XPUB_2+"foo"));
  }

  @Test
  public void xlatXpub() throws Exception {
    final String TPUB_1 = "tpubD6NzVbkrYhZ4WaWSyoBvQwbpLkojyoTZPRsgXELWz3Popb3qkjcJyJUGLnL4qHHoQvao8ESaAstxYSnhyswJ76uZPStJRJCTKvosUCJZL5B";
    final String UPUB_1 = "upub57Wa4MvRPNyAgtkF2XqxakywVjGkAYz16TiipVbiW7WGuzwSvYGXxfq238NXK4NoQ6hUGE92Fo1GCQTQRvr1pxQTiq3iz35kvo2XYU7ZfFa";
    Assertions.assertEquals(TPUB_1, formatsUtil.xlatXpub(VPUB_1, true));
    Assertions.assertEquals(TPUB_1, formatsUtil.xlatXpub(VPUB_1, false));
    Assertions.assertEquals(VPUB_1, formatsUtil.xlatXpub(TPUB_1, true));
    Assertions.assertEquals(UPUB_1, formatsUtil.xlatXpub(TPUB_1, false));
    Assertions.assertEquals(TPUB_1, formatsUtil.xlatXpub(UPUB_1, true));
    Assertions.assertEquals(TPUB_1, formatsUtil.xlatXpub(UPUB_1, false));

    final String TPUB_2 = "tpubDF3Dw2nZnTYgxzXqxb8a4samW4kJTy64JNCUpUP1SeMXDdbh6ekDKCBvJHDBUf6itTccJ1asSTWQEDwVuWVRDNTUs3inqJcJuMQZk8EysmY";
    final String UPUB_2 = "upub5GAoVnx8d8xo9Jme1KncEgxtf3DJeicW1Q3X7jeCxiTzK3VJGTQSJZYfzdFdxSBisdjHS1HKXNchtBcCMZQ8wDxPCRtDQ3VcWDdDpJ4zA7C";
    Assertions.assertEquals(TPUB_2, formatsUtil.xlatXpub(VPUB_2, true));
    Assertions.assertEquals(TPUB_2, formatsUtil.xlatXpub(VPUB_2, false));
    Assertions.assertEquals(VPUB_2, formatsUtil.xlatXpub(TPUB_2, true));
    Assertions.assertEquals(UPUB_2, formatsUtil.xlatXpub(TPUB_2, false));
    Assertions.assertEquals(TPUB_2, formatsUtil.xlatXpub(UPUB_2, true));
    Assertions.assertEquals(TPUB_2, formatsUtil.xlatXpub(UPUB_2, false));

    final String ZPUB_1 = "zpub6qo73p51R32Ennq46njyqsCsjsxfjoJND5iRCseXUkT1veJApxmVh9X6XkpPzE2ZdumnGjApyy3ddJ7d8wZgn91SEynNeg7CmjyuTuXfNQ6";
    final String YPUB_1 = "ypub6Wxqk9Q6GMUkwVdwGRxMdn7NZupDoBJsHyCCRUke6k58sYUwaJbw55rxWYrozKNeEGeyXFaGXJh5k1W4RF9fyuKqNe5x4mHiW1vG5JX2Wmv";
    Assertions.assertEquals(ZPUB_1, formatsUtil.xlatXpub(XPUB_1, true));
    Assertions.assertEquals(YPUB_1, formatsUtil.xlatXpub(XPUB_1, false));
    Assertions.assertEquals(XPUB_1, formatsUtil.xlatXpub(ZPUB_1, true));
    Assertions.assertEquals(XPUB_1, formatsUtil.xlatXpub(ZPUB_1, false));
    Assertions.assertEquals(XPUB_1, formatsUtil.xlatXpub(YPUB_1, true));
    Assertions.assertEquals(XPUB_1, formatsUtil.xlatXpub(YPUB_1, false));

  }
}
