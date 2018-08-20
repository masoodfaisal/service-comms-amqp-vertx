package com.faisal.vertx.amqp.frontend;


import io.vertx.amqpbridge.AmqpBridge;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;


public class AmqpSender extends AbstractVerticle {


    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();
        AmqpBridge amqpBridge = AmqpBridge.create(vertx);


        amqpBridge.start(AMQP_SERVER_LOCATION, AMQP_SERVER_PORT, amqpBridgeStartResult -> {

            if (amqpBridgeStartResult.succeeded()) {
                MessageProducer<JsonObject> amqpMessageProducer = amqpBridge.createProducer(AMQP_ADDRESS);
                eventBus.consumer(SEND_REQUEST_TO_BACKEND_CHANNEL, (Handler<Message<JsonObject>>) message -> {
                    JsonObject sourceEventData = message.body();
                    JsonObject amqpData = new JsonObject()
                            .put("body", sourceEventData);
                    System.out.println("Sending --- " + sourceEventData);
                    amqpMessageProducer
                            .send(amqpData, (Handler<AsyncResult<Message<JsonObject>>>) messageAsyncResult -> {
                                System.out.println("Got response ---" + messageAsyncResult.result());
                                System.out.println("Got response BODY---" + messageAsyncResult.result().body());
                                JsonObject responseFromAMQPServer = messageAsyncResult.result().body();
                                message.reply(responseFromAMQPServer);
                            });


                });
            } else {
                System.out.printf(amqpBridgeStartResult.cause().getMessage());
            }

        });


    }


    private static final String AMQP_ADDRESS = "BACKEND_SERVICE";

    public static final String SEND_REQUEST_TO_BACKEND_CHANNEL = "LOCAL_EVENT_BUS";
    private final String AMQP_SERVER_LOCATION = "localhost";
    private final int AMQP_SERVER_PORT = 5672;
}



