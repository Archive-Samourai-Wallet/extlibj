package com.samourai.wallet.api.backend.beans;

public class HttpException extends Exception {
  private String responseBody;
  private int statusCode;

  public HttpException(Exception cause, String responseBody, int statusCode) {
    super(cause);
    this.responseBody = responseBody;
    this.statusCode = statusCode;
  }

  public HttpException(String message, String responseBody, int statusCode) {
    super(message);
    this.responseBody = responseBody;
    this.statusCode = statusCode;
  }

  public HttpException(Exception cause) {
    this(cause, null, 500);
  }

  public HttpException(String message) {
    this(message, null, 500);
  }

  public String getResponseBody() {
    return responseBody;
  }

  public int getStatusCode() {
    return statusCode;
  }

  @Override
  public String toString() {
    return "HttpException{" +
            "message=" + getMessage() + ", " +
            "responseBody='" + responseBody + '\'' +
            '}';
  }
}
