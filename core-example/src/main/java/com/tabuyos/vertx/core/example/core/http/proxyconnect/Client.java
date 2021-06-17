/*
 * Copyright (c) 2018-2021 Tabuyos All Right Reserved.
 */
package com.tabuyos.vertx.core.example.core.http.proxyconnect;

import com.tabuyos.vertx.core.example.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;

/**
 * @author <a href="http://www.tabuyos.com">tabuyos</a>
 * @since 2021/6/15
 */
public class Client extends AbstractVerticle {

  /**
   * Convenience method so you can run it in your IDE
   *
   * @param args args
   */
  public static void main(String[] args) {
    Runner.runExample(Client.class);
  }

  @Override
  public void start() throws Exception {
    HttpClientOptions options =
        new HttpClientOptions()
            .setSsl(true)
            .setTrustAll(true)
            .setVerifyHost(false)
            .setProxyOptions(
                new ProxyOptions().setType(ProxyType.HTTP).setHost("localhost").setPort(8080));
    HttpClient client = vertx.createHttpClient(options);
    client
        .request(HttpMethod.GET, 8282, "localhost", "/")
        .compose(
            request -> {
              request.setChunked(true);
              for (int i = 0; i < 10; i++) {
                request.write("client-chunk-" + i);
              }
              request.end();
              return request
                  .response()
                  .compose(
                      resp -> {
                        System.out.println("Got response " + resp.statusCode());
                        return resp.body();
                      });
            })
        .onSuccess(body -> System.out.println("Got data " + body.toString("ISO-8859-1")))
        .onFailure(Throwable::printStackTrace);
  }
}
