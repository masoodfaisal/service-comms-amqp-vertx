package com.faisal.vertx.amqp.backend;


import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.amqpbridge.AmqpBridge;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.MessageConsumer;


public class AmqpServer extends AbstractVerticle {


    @Override
    public void start() {

        AmqpBridge amqpBridge = AmqpBridge.create(vertx);

        amqpBridge.rxStart(AMQP_SERVER_LOCATION, AMQP_SERVER_PORT)
                .doOnSuccess(amqpBridge1 -> this.processMessage(amqpBridge.createConsumer(AMQP_ADDRESS)))
                .doOnError(throwable -> System.out.println(throwable.getCause().getMessage()))
                .subscribe();

    }


    public void processMessage(MessageConsumer<JsonObject> messageConsumer) {
        messageConsumer
                .handler(event -> {
                    JsonObject received = event.body().getJsonObject("body");
                    System.out.println("Service Received " + received);
                    event.reply(new JsonObject()
                            .put("body", "Sending back the response for " + received.getString("eventName") + " with id " + received.getString("eventId")
                            ));
                });
    }


    private final String AMQP_ADDRESS = "BACKEND_SERVICE";
    private final String AMQP_SERVER_LOCATION = "localhost";
    private final int AMQP_SERVER_PORT = 5672;
}
