package com.faisal.vertx.amqp.backend;


import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;


public class Main {

    public static void main(String[] args) {
        RxHelper.deployVerticle(Vertx.vertx(), new AmqpServer())
                .subscribe(s -> System.out.println("Started....."));

    }


}
