/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.middleware;

import com.jetdrone.vertx.yoke.AbstractMiddleware;
import com.jetdrone.vertx.yoke.Middleware;
import org.jetbrains.annotations.NotNull;
import org.vertx.java.core.Handler;

/** # ResponseTime
 *
 * Adds the ```x-response-time``` header displaying the response duration in milliseconds.
 */
public class ResponseTime extends AbstractMiddleware {
    @Override
    public void handle(@NotNull final YokeRequest request, @NotNull final Handler<Object> next) {

        final long start = System.currentTimeMillis();
        final YokeResponse response = request.response();

        response.headersHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                long duration = System.currentTimeMillis() - start;
                response.putHeader("x-response-time", duration + "ms");
            }
        });

        next.handle(null);
    }
}
