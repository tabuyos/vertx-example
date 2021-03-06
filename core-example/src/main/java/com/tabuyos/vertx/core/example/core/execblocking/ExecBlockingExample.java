/*
 * Copyright (c) 2018-2021 Tabuyos All Right Reserved.
 */
package com.tabuyos.vertx.core.example.core.execblocking;

import com.tabuyos.vertx.core.example.util.Runner;
import io.vertx.core.AbstractVerticle;

/**
 * @author <a href="http://www.tabuyos.com">tabuyos</a>
 * @since 2021/6/15
 */
public class ExecBlockingExample extends AbstractVerticle {

  /**
   * Convenience method so you can run it in your IDE
   *
   * @param args args
   */
  public static void main(String[] args) {
    Runner.runExample(ExecBlockingExample.class);
  }

  @Override
  public void start() throws Exception {

    vertx
        .createHttpServer()
        .requestHandler(
            request -> {

              // Let's say we have to call a blocking API (e.g. JDBC) to execute a query for each
              // request. We can't do this directly or it will block the event loop
              // But you can do this using executeBlocking:

              vertx.<String>executeBlocking(
                  promise -> {

                    // Do the blocking operation in here

                    // Imagine this was a call to a blocking API to get the result
                    try {
                      Thread.sleep(500);
                    } catch (Exception ignore) {
                    }
                    String result = "armadillos!";

                    promise.complete(result);
                  },
                  res -> {
                    if (res.succeeded()) {

                      request.response().putHeader("content-type", "text/plain").end(res.result());

                    } else {
                      res.cause().printStackTrace();
                    }
                  });
            })
        .listen(8080);
  }
}
