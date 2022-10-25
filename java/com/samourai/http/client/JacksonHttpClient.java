package com.samourai.http.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samourai.wallet.api.backend.beans.HttpException;
import com.samourai.wallet.util.AsyncUtil;
import com.samourai.wallet.util.JSONUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

public abstract class JacksonHttpClient implements IHttpClient {
  private static final Logger log = LoggerFactory.getLogger(JacksonHttpClient.class);

  public JacksonHttpClient() {
  }

  protected abstract String requestJsonGet(String urlStr, Map<String, String> headers, boolean async)
      throws Exception;

  protected abstract String requestJsonPost(
      String urlStr, Map<String, String> headers, String jsonBody) throws Exception;

  protected abstract String requestJsonPostUrlEncoded(
      String urlStr, Map<String, String> headers, Map<String, String> body) throws Exception;

  @Override
  public <T> T getJson(String urlStr, Class<T> responseType, Map<String, String> headers)
      throws HttpException {
      return getJson(urlStr, responseType, headers, false);
  }

  @Override
  public <T> T getJson(String urlStr, Class<T> responseType, Map<String, String> headers, boolean async)
          throws HttpException {
    return httpObservableBlockingSingle(() -> { // run on ioThread
      try {
        String responseContent = requestJsonGet(urlStr, headers, async);
        T result = parseJson(responseContent, responseType);
        return result;
      } catch (Exception e) {
        if (log.isDebugEnabled()) {
          if (e instanceof HttpException) {
            log.error("getJson failed: " + urlStr + ": " + e.toString());
          } else {
            log.error("getJson failed: " + urlStr, e);
          }
        }
        throw httpException(e);
      }
    });
  }

  @Override
  public <T> Single<Optional<T>> postJson(
      final String urlStr,
      final Class<T> responseType,
      final Map<String, String> headers,
      final Object bodyObj) {
    return httpObservable(
            () -> {
              try {
                String jsonBody = getObjectMapper().writeValueAsString(bodyObj);
                String responseContent = requestJsonPost(urlStr, headers, jsonBody);
                T result = parseJson(responseContent, responseType);
                return result;
              } catch (Exception e) {
                if (log.isDebugEnabled()) {
                  if (e instanceof HttpException) {
                    log.error("postJson failed: " + urlStr + ": " + e.toString());
                  } else {
                    log.error("postJson failed: " + urlStr, e);
                  }
                }
                throw httpException(e);
              }
            });
  }

  @Override
  public <T> T postUrlEncoded(
      String urlStr, Class<T> responseType, Map<String, String> headers, Map<String, String> body)
      throws HttpException {
    return httpObservableBlockingSingle(() -> { // run on ioThread
      try {
        String responseContent = requestJsonPostUrlEncoded(urlStr, headers, body);
        T result = parseJson(responseContent, responseType);
        return result;
      } catch (Exception e) {
        if (log.isDebugEnabled()) {
          if (e instanceof HttpException) {
            log.error("postUrlEncoded failed: " + urlStr + ": " + e.toString());
          } else {
            log.error("postUrlEncoded failed: " + urlStr, e);
          }
        }
        throw httpException(e);
      }
    });
  }

  private <T> T parseJson(String responseContent, Class<T> responseType) throws Exception {
    T result;
    if (log.isTraceEnabled()) {
      String responseStr =
          (responseContent != null
              ? responseContent.substring(0, Math.min(responseContent.length(), 50))
              : "null");
      log.trace(
          "response["
              + (responseType != null ? responseType.getCanonicalName() : "null")
              + "]: "
              + responseStr);
    }
    if (String.class.equals(responseType)) {
      result = (T) responseContent;
    } else {
      result = getObjectMapper().readValue(responseContent, responseType);
    }
    return result;
  }

  protected HttpException httpException(Exception e) {
    if (!(e instanceof HttpException)) {
      return new HttpException(e, null);
    }
    return (HttpException) e;
  }

  protected <T> Single<Optional<T>> httpObservable(final Callable<T> supplier) {
    return Single.fromCallable(() -> Optional.ofNullable(supplier.call())).subscribeOn(Schedulers.io());
  }

  protected <T> T httpObservableBlockingSingle(final Callable<T> supplier) throws HttpException{
    try {
      Optional<T> opt = AsyncUtil.getInstance().blockingGet(
              httpObservable(supplier)
      );
      return opt.orElse(null);
    } catch (Exception e) {
      throw httpException(e);
    }
  }

  protected ObjectMapper getObjectMapper() {
    return JSONUtils.getInstance().getObjectMapper();
  }
}
