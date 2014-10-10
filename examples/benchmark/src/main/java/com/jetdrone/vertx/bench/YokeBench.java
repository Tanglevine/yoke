package com.jetdrone.vertx.bench;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.BodyParser;
import com.jetdrone.vertx.yoke.middleware.Router;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import io.vertx.core.AbstractVerticle;
import org.jetbrains.annotations.NotNull;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Handler;

public class YokeBench extends AbstractVerticle {

    @Override
    public void start() {

        final Middleware foo = (request, next) -> next.handle(null);

        new Yoke(vertx)
                .use(new BodyParser())
                .use("/middleware", foo)
                .use("/middleware", foo)
                .use("/middleware", foo)
                .use("/middleware", foo)
                .use(new Router()
                        .get("/", request -> request.response().end("Hello World\n"))
                        .get("/json", request -> request.response().end(new JsonObject().putString("name", "Tobi").putString("role", "admin")))
                        .get("/middleware", request -> request.response().end("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"))
                ).listen(8080);
    }
}
