/*
 * Copyright (c) 2018-2021 Tabuyos All Right Reserved.
 */
package com.tabuyos.vertx.core.example.core.net.stream;

import com.tabuyos.vertx.core.example.util.Runner;
import io.vertx.core.AbstractVerticle;

/**
 * @author <a href="http://www.tabuyos.com">tabuyos</a>
 * @since 2021/6/15
 */
public class Server extends AbstractVerticle {

  /**
   * Convenience method so you can run it in your IDE
   *
   * @param args args
   */
  public static void main(String[] args) {
    Runner.runExample(Server.class);
  }

  @Override
  public void start() throws Exception {
    vertx
        .createNetServer()
        .connectHandler(
            socket -> {

              // Create batch stream for reading and writing
              BatchStream batchStream = new BatchStream(socket, socket);

              // Pause reading data
              batchStream.pause();

              // Register read stream handler
              batchStream
                  .handler(
                      batch -> {

                        // Print received batch object from the client
                        System.out.println("Server Received : " + batch.getRaw().toString());

                        // Write back batch object to the client
                        batchStream.write(batch);

                        // Check if write queue is full
                        if (batchStream.writeQueueFull()) {

                          // Pause reading data
                          batchStream.pause();

                          // Called once write queue is ready to accept more data
                          batchStream.drainHandler(
                              done -> {

                                // Resume reading data
                                batchStream.resume();
                              });
                        }
                      })
                  .endHandler(v -> batchStream.end())
                  .exceptionHandler(
                      t -> {
                        t.printStackTrace();
                        batchStream.end();
                      });

              // Resume reading data
              batchStream.resume();
            })
        .listen(1234);
    System.out.println("Batch server is now listening to port : 1234");
  }
}
