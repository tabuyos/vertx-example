/*
 * Copyright (c) 2018-2021 Tabuyos All Right Reserved.
 */
package com.tabuyos.vertx.core.example.core.eventbus.messagecodec;

import com.tabuyos.vertx.core.example.core.eventbus.messagecodec.util.CustomMessage;
import com.tabuyos.vertx.core.example.core.eventbus.messagecodec.util.CustomMessageCodec;
import com.tabuyos.vertx.core.example.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

/**
 * Publisher
 *
 * @author <a href="http://www.tabuyos.com">tabuyos</a>
 * @since 2021/6/15
 */
public class Sender extends AbstractVerticle {
  public static void main(String[] args) {
    Runner.runClusteredExample(Sender.class);
  }

  @Override
  public void start() throws Exception {
    EventBus eventBus = getVertx().eventBus();

    // Register codec for custom message
    eventBus.registerDefaultCodec(CustomMessage.class, new CustomMessageCodec());

    // Custom message
    CustomMessage clusterWideMessage =
        new CustomMessage(200, "a00000001", "Message sent from publisher!");
    CustomMessage localMessage = new CustomMessage(200, "a0000001", "Local message!");

    // Send a message to [cluster receiver] every second
    getVertx()
        .setPeriodic(
            1000,
            _id -> {
              eventBus.request(
                  "cluster-message-receiver",
                  clusterWideMessage,
                  reply -> {
                    if (reply.succeeded()) {
                      CustomMessage replyMessage = (CustomMessage) reply.result().body();
                      System.out.println("Received reply: " + replyMessage.getSummary());
                    } else {
                      System.out.println("No reply from cluster receiver");
                    }
                  });
            });

    // Deploy local receiver
    getVertx()
        .deployVerticle(
            LocalReceiver.class.getName(),
            deployResult -> {
              // Deploy succeed
              if (deployResult.succeeded()) {
                // Send a message to [local receiver] every 2 second
                getVertx()
                    .setPeriodic(
                        2000,
                        _id -> {
                          eventBus.request(
                              "local-message-receiver",
                              localMessage,
                              reply -> {
                                if (reply.succeeded()) {
                                  CustomMessage replyMessage =
                                      (CustomMessage) reply.result().body();
                                  System.out.println(
                                      "Received local reply: " + replyMessage.getSummary());
                                } else {
                                  System.out.println("No reply from local receiver");
                                }
                              });
                        });

                // Deploy failed
              } else {
                deployResult.cause().printStackTrace();
              }
            });
  }
}
