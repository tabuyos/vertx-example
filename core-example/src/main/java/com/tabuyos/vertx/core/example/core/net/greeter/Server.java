/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/*
 * Copyright (c) 2018-2021 Tabuyos All Right Reserved.
 */
package com.tabuyos.vertx.core.example.core.net.greeter;

import com.tabuyos.vertx.core.example.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.parsetools.RecordParser;

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
            sock -> {
              RecordParser parser = RecordParser.newDelimited("\n", sock);

              parser
                  .endHandler(v -> sock.close())
                  .exceptionHandler(
                      t -> {
                        t.printStackTrace();
                        sock.close();
                      })
                  .handler(
                      buffer -> {
                        String name = buffer.toString("UTF-8");
                        sock.write("Hello " + name + "\n", "UTF-8");
                      });
            })
        .listen(1234);

    System.out.println("Echo server is now listening");
  }
}
