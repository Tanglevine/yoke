/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.middleware;

import org.jetbrains.annotations.NotNull;
import io.vertx.core.Handler;

/** # Timeout
 *
 * Times out the request in ```ms```, defaulting to ```5000```.
 *
 * The timeout error is passed to ```next.handle(408)``` so that you may customize the response behaviour.
 */
public class Timeout extends AbstractMiddleware {

    private final long timeout;

    public Timeout(final long timeout) {
        this.timeout = timeout;
    }

    public Timeout() {
        this(5000);
    }
    @Override
    public void handle(@NotNull final YokeRequest request, @NotNull final Handler<Object> next) {
        final YokeResponse response = request.response();

        final long timerId = vertx().setTimer(timeout, new Handler<Long>() {
            @Override
            public void handle(Long event) {
                next.handle(408);
            }
        });

        response.endHandler(event -> vertx().cancelTimer(timerId));

        next.handle(null);
    }
}
