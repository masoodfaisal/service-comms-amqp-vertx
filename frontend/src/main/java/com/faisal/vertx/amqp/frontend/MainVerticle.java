package com.faisal.vertx.amqp.frontend;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.time.Instant;
import java.util.UUID;
import java.util.function.BiConsumer;


public class MainVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new MainVerticle());
        vertx.deployVerticle(new AmqpSender());


    }

    @Override
    public void start() {

        Router router = Router.router(vertx);
        router.get("/").handler(this::backEndServiceHandler);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);


    }


    private void backEndServiceHandler(RoutingContext routingContext) {
        EventBus eventBus = vertx.eventBus();
        JsonObject jsonObject = new JsonObject()
                .put("eventName", "myEvent")
                .put("eventId", UUID.randomUUID().toString())
                .put("eventGeneratedTime", Instant.now().toEpochMilli());


        eventBus.send(AmqpSender.SEND_REQUEST_TO_BACKEND_CHANNEL, jsonObject, (Handler<AsyncResult<Message<JsonObject>>>) asyncResult -> {
            if (asyncResult.succeeded()) {
                JsonObject eventDataResponse = asyncResult.result().body();
                routingContext.response().end(eventDataResponse.encode());
            } else {
                routingContext.response().end("Some shit happened");
            }

        });



//        eventBus.send(AmqpSender.SEND_REQUEST_TO_BACKEND_CHANNEL, jsonObject, (AsyncResult<Message<JsonObject>> asyncResult) -> this.getResponse(asyncResult, routingContext));


    }


/*    private void getResponse(AsyncResult<Message<JsonObject>> asyncResult, RoutingContext routingContext){
        if (asyncResult.succeeded()) {
            JsonObject eventDataResponse = asyncResult.result().body();
            routingContext.response().end(eventDataResponse.encode());
        } else {
            routingContext.response().end("Some shit happened");
        }
    }*/






}
