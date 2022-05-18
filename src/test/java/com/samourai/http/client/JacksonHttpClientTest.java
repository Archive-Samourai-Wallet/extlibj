package com.samourai.http.client;

import com.samourai.wallet.api.backend.beans.HttpException;
import com.samourai.wallet.api.paynym.beans.PaynymErrorResponse;
import com.samourai.wallet.test.AbstractTest;
import com.samourai.wallet.util.AsyncUtil;
import com.samourai.wallet.util.JSONUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class JacksonHttpClientTest extends AbstractTest {

  private JacksonHttpClient httpClient;
  private String mockResponse;
  private Exception mockException;

  @BeforeEach
  public void setUp() throws Exception{
    super.setUp();
    this.mockException = null;
  }

  public JacksonHttpClientTest() throws Exception {
    PaynymErrorResponse mock = new PaynymErrorResponse();
    mock.message = "test";
    mockResponse = JSONUtils.getInstance().getObjectMapper().writeValueAsString(mock);

    httpClient = new JacksonHttpClient() {
      @Override
      protected String requestJsonGet(String urlStr, Map<String, String> headers, boolean async) throws Exception {
        if (mockException != null) {
          throw mockException;
        }
        return mockResponse;
      }

      @Override
      protected String requestJsonPost(String urlStr, Map<String, String> headers, String jsonBody) throws Exception {
        if (mockException != null) {
          throw mockException;
        }
        return mockResponse;
      }

      @Override
      protected String requestJsonPostUrlEncoded(String urlStr, Map<String, String> headers, Map<String, String> body) throws Exception {
        if (mockException != null) {
          throw mockException;
        }
        return mockResponse;
      }

      @Override
      public void connect() throws Exception {}
    };
  }

  @Test
  public void getJson() throws Exception {
    // success
    PaynymErrorResponse response = httpClient.getJson("http://test", PaynymErrorResponse.class, null);
    Assertions.assertEquals("test", response.message);

    // success: String instead of PaynymErrorResponse - parseJson()
    String stringResponse = httpClient.getJson("http://test", String.class, null);
    Assertions.assertEquals("{\"message\":\"test\"}", stringResponse);

    //exception
    mockException = new IllegalArgumentException("test");
    try {
      httpClient.getJson("http://test", PaynymErrorResponse.class, null);
    } catch (HttpException e) {
      Assertions.assertEquals("test", e.getCause().getMessage());
    }

    //exception: String instead of PaynymErrorResponse - parseJson()
    try {
      httpClient.getJson("http://test", String.class, null);
    } catch (HttpException e) {
      Assertions.assertEquals("test", e.getCause().getMessage());
    }
  }

  @Test
  public void postJson() throws Exception {
    // success
    PaynymErrorResponse response = AsyncUtil.getInstance().blockingSingle(httpClient.postJson("http://test", PaynymErrorResponse.class, null, null)).get();
    Assertions.assertEquals("test", response.message);

    // success: String instead of PaynymErrorResponse - parseJson()
    String stringResponse = AsyncUtil.getInstance().blockingSingle(httpClient.postJson("http://test", String.class, null, null)).get();
    Assertions.assertEquals("{\"message\":\"test\"}", stringResponse);

    //exception
    mockException = new IllegalArgumentException("test");
    try {
      AsyncUtil.getInstance().blockingSingle(httpClient.postJson("http://test", PaynymErrorResponse.class, null, null)).get();
    } catch (HttpException e) {
      Assertions.assertEquals("test", e.getCause().getMessage());
    }

    //exception: String instead of PaynymErrorResponse - parseJson()
    try {
      AsyncUtil.getInstance().blockingSingle(httpClient.postJson("http://test", String.class, null, null)).get();
    } catch (HttpException e) {
      Assertions.assertEquals("test", e.getCause().getMessage());
    }
  }

  @Test
  public void postUrlEncoded() throws Exception {
    // success
    PaynymErrorResponse response = httpClient.postUrlEncoded("http://test", PaynymErrorResponse.class, null, null);
    Assertions.assertEquals("test", response.message);

    // success: String instead of PaynymErrorResponse - parseJson()
    String stringResponse = httpClient.postUrlEncoded("http://test", String.class, null, null);
    Assertions.assertEquals("{\"message\":\"test\"}", stringResponse);

    //exception
    mockException = new IllegalArgumentException("test");
    try {
      httpClient.postUrlEncoded("http://test", PaynymErrorResponse.class, null, null);
    } catch (HttpException e) {
      Assertions.assertEquals("test", e.getCause().getMessage());
    }

    //exception: String instead of PaynymErrorResponse - parseJson()
    try {
      httpClient.postUrlEncoded("http://test", String.class, null, null);
    } catch (HttpException e) {
      Assertions.assertEquals("test", e.getCause().getMessage());
    }
  }
}
