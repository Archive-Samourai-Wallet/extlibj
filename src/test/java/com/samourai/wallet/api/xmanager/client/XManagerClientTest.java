package com.samourai.wallet.api.xmanager.client;

import com.samourai.http.client.JettyHttpClient;
import com.samourai.wallet.api.backend.BackendApi;
import com.samourai.wallet.api.backend.beans.UnspentOutput;
import com.samourai.wallet.bipFormat.BIP_FORMAT;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.send.beans.SweepPreview;
import com.samourai.wallet.test.AbstractTest;
import com.samourai.wallet.util.PrivKeyReader;
import com.samourai.xmanager.client.XManagerClient;
import com.samourai.xmanager.protocol.XManagerService;
import com.samourai.xmanager.protocol.rest.AddressIndexResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class XManagerClientTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(XManagerClientTest.class);

    @Test
    public void addressFetchTest() throws Exception {
        httpClient = new JettyHttpClient(10000, Optional.empty(), "test");

        XManagerClient xManagerClient = new XManagerClient(httpClient, true, false);
        String address = xManagerClient.getAddressOrDefault(XManagerService.SAAS);
        System.out.println(address);
    }
}
