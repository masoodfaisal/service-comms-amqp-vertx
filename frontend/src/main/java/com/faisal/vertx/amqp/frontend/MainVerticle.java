package com.faisal.vertx.amqp.frontend;


import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Single;

import java.time.Instant;
import java.util.UUID;


public class MainVerticle extends AbstractVerticle {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();


        RxHelper.deployVerticle(vertx, new AmqpSender())
                .toCompletable()
                .andThen(RxHelper.deployVerticle(vertx, new MainVerticle()))
                .subscribe(s -> System.out.println("Started..."));
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


        eventBus.rxSend(AmqpSender.SEND_REQUEST_TO_BACKEND_CHANNEL, jsonObject)

                .doOnError(throwable -> routingContext.response().end(throwable.getMessage()))
                .doOnSuccess(objectMessage -> routingContext.response().end(objectMessage.body().toString()))
                .subscribe();
    }


}
