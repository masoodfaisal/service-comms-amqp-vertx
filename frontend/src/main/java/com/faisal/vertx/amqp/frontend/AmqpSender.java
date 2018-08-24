package com.faisal.vertx.amqp.frontend;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.amqpbridge.AmqpBridge;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import io.vertx.rxjava.core.eventbus.MessageProducer;

public class AmqpSender extends AbstractVerticle {


    @Override
    public void start() {

        AmqpBridge amqpBridge = AmqpBridge.create(vertx);


        amqpBridge.rxStart(AMQP_SERVER_LOCATION, AMQP_SERVER_PORT)
                .doOnSuccess(amqpBridge1 -> this.eventResponse(amqpBridge1.createProducer(AMQP_ADDRESS),
                        vertx.eventBus().consumer(SEND_REQUEST_TO_BACKEND_CHANNEL)))
                .doOnError(throwable -> System.out.printf(throwable.getMessage()))
                .subscribe();


    }

    public void eventResponse(MessageProducer<JsonObject> amqpMessageProducer, MessageConsumer<JsonObject> messageConsumer) {
        messageConsumer.toObservable()
                .doOnError(throwable -> System.out.printf(throwable.getCause().getMessage()))
                .subscribe(jsonObjectMessage -> this.makeRemoteCall(amqpMessageProducer, jsonObjectMessage));
    }


    public void makeRemoteCall(MessageProducer<JsonObject> amqpMessageProducer, Message<JsonObject> jsonObjectMessage){
        JsonObject amqpData = new JsonObject().put("body", jsonObjectMessage.body());
        amqpMessageProducer.send(amqpData, (Handler<AsyncResult<Message<JsonObject>>>) messageAsyncResult -> {
                    System.out.println("Got response ---" + messageAsyncResult.result());
                    System.out.println("Got response BODY---" + messageAsyncResult.result().body());
                    JsonObject responseFromAMQPServer = messageAsyncResult.result().body();
                    jsonObjectMessage.reply(new JsonObject().put("serverResponse", responseFromAMQPServer.getValue("body")));
                });
    }


    private static final String AMQP_ADDRESS = "BACKEND_SERVICE";

    public static final String SEND_REQUEST_TO_BACKEND_CHANNEL = "LOCAL_EVENT_BUS";
    private static final String AMQP_SERVER_LOCATION = env("AMQP_SERVER_LOCATION", "localhost");
    private static final int AMQP_SERVER_PORT = 5672;

    private static String env(final String key, final String defaultValue) {

        final String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }
}



