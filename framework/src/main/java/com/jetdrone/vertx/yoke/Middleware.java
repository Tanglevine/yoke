/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke;

import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import org.jetbrains.annotations.NotNull;
import org.vertx.java.core.Handler;

/**
 * # Middleware
 */
@FunctionalInterface
public interface Middleware extends Handler<YokeRequest> {

    default void handle(@NotNull final YokeRequest request) {
        handle(request, null);
    }

    /**
     * Handles a request that is inside the chain.
     *
     * Example that always returns Hello:
     * <pre>
     * class HelloMiddleware extends Middleware {
     *   public void handle(YokeRequest request, Handler&lt;Object&gt; next) {
     *     request.response.end("Hello");
     *   }
     * }
     * </pre>
     *
     * Example that always raises an internal server error:
     * <pre>
     * class HelloMiddleware extends Middleware {
     *   public void handle(YokeRequest request, Handler&lt;Object&gt; next) {
     *     next.handle("Something went wrong!");
     *   }
     * }
     * </pre>
     *
     * Example that passes the control to the next middleware:
     * <pre>
     * class HelloMiddleware extends Middleware {
     *   public void handle(YokeRequest request, Handler&lt;Object&gt; next) {
     *     // when the error is null, then the chain will execute
     *     // the next Middleware until the chain is complete,
     *     // when that happens a 404 error is returned since no
     *     // middleware was found that could handle the request.
     *     next.handle(null);
     *   }
     * }
     * </pre>
     *
     * @param request A YokeRequest which in practice is a extended HttpServerRequest
     * @param next    The callback to inform that the next middleware in the chain should be used. A value different from
     *                null represents an error and in that case the error handler middleware will be executed.
     */
    void handle(@NotNull final YokeRequest request, @NotNull final Handler<Object> next);
}
