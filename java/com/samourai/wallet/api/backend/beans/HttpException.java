package com.samourai.wallet.api.backend.beans;

public class HttpException extends Exception {
  private String responseBody;

  public HttpException(Exception cause, String responseBody) {
    super(cause);
    this.responseBody = responseBody;
  }

  public HttpException(String message, String responseBody) {
    super(message);
    this.responseBody = responseBody;
  }

  public HttpException(Exception cause) {
    this(cause, null);
  }

  public HttpException(String message) {
    this(message, null);
  }

  public String getResponseBody() {
    return responseBody;
  }

  @Override
  public String toString() {
    return "HttpException{" +
            "message=" + getMessage() + ", " +
            "responseBody='" + responseBody + '\'' +
            '}';
  }
}
