package com.jetdrone.vertx.yoke.middleware;

import com.jetdrone.vertx.yoke.AbstractMiddleware;
import com.jetdrone.vertx.yoke.Middleware;
import org.jetbrains.annotations.NotNull;
import org.vertx.java.core.Handler;

/**
 *  X-Content-Type-Options
 * The only defined value, "nosniff", prevents Internet Explorer from MIME-sniffing a response away from the declared content-type
 */
public final class ContentTypeOptions extends AbstractMiddleware {

    @Override
    public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
        request.response().putHeader("X-Content-Type-Options", "nosniff");
        next.handle(null);
    }
}
