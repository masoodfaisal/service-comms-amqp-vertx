package com.faisal.vertx.amqp.backend;


import io.vertx.amqpbridge.AmqpBridge;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;


public class AmqpServer extends AbstractVerticle {


    @Override
    public void start() {

        AmqpBridge amqpBridge = AmqpBridge.create(vertx);

        amqpBridge.start(AMQP_SERVER_LOCATION, AMQP_SERVER_PORT, amqpBridgeStartResult -> {
            if (amqpBridgeStartResult.succeeded()) {
                MessageConsumer<JsonObject> consumer = amqpBridge.createConsumer(AMQP_ADDRESS);

                consumer.handler(message -> {
                    JsonObject received = message.body().getJsonObject("body");

                    System.out.println("Service Received " + received);

                    message.reply(new JsonObject()
                            .put("body", "Sending back the response for " + received.getString("eventName") + " with id " + received.getString("eventId")
                    ));


                });

            } else {
                System.out.println(amqpBridgeStartResult.cause());
            }


        });


    }


    private final String AMQP_ADDRESS = "BACKEND_SERVICE";
    private final String AMQP_SERVER_LOCATION = "localhost";
    private final int AMQP_SERVER_PORT=5672;
}
