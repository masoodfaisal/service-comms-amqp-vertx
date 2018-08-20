package com.faisal.vertx.amqp.backend;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;


public class MainVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();


        vertx.deployVerticle(new AmqpServer());


    }


}
