/*
 * Copyright (c) 2018-2021 Tabuyos All Right Reserved.
 */
package com.tabuyos.vertx.core.example.core.eventbus.messagecodec.util;

/**
 * Custom message for example
 *
 * @author <a href="http://www.tabuyos.com">tabuyos</a>
 * @since 2021/6/15
 */
public class CustomMessage {
  private final int statusCode;
  private final String resultCode;
  private final String summary;

  public CustomMessage(int statusCode, String resultCode, String summary) {
    this.statusCode = statusCode;
    this.resultCode = resultCode;
    this.summary = summary;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("CustomMessage{");
    sb.append("statusCode=").append(statusCode);
    sb.append(", resultCode='").append(resultCode).append('\'');
    sb.append(", summary='").append(summary).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getResultCode() {
    return resultCode;
  }

  public String getSummary() {
    return summary;
  }
}
