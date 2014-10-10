package com.jetdrone.vertx;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.*;
import com.jetdrone.vertx.yoke.store.MongoDBSessionStore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;

import javax.crypto.Mac;

public class SessionStoreExample extends AbstractVerticle {

    @Override
    public void start() {
        // load the general config object, loaded by using -config on command line
        JsonObject config = new JsonObject();
        config.putString("connection_string", "mongodb://localhost");
        config.putString("db_name", "yoke3-demo");

        // deploy the mongo-persistor module, which we'll use for persistence

        MongoService mongo = MongoService.create(vertx, config);
        mongo.start();

        final Yoke app = new Yoke(SessionStoreExample.this);
        app.secretSecurity("keyboard cat");

        app.store(new MongoDBSessionStore(mongo, "sessions"));

        final Mac hmac = app.security().getMac("HmacSHA256");

        app.use(new BodyParser());
        app.use(new CookieParser(hmac));
        app.use(new Session(hmac));


        app.use(new Router() {{
            get("/", new Handler<YokeRequest>() {
                @Override
                public void handle(YokeRequest request) {
                    JsonObject session = request.get("session");
                    if (session == null) {
                        request.response().setStatusCode(404);
                        request.response().end();
                    } else {
                        request.response().end(session);
                    }
                }
            });

            get("/new", new Handler<YokeRequest>() {
                @Override
                public void handle(YokeRequest request) {
                    JsonObject session = request.createSession();

                    session.putString("key", "value");

                    request.response().end();
                }
            });

            get("/delete", new Handler<YokeRequest>() {
                @Override
                public void handle(YokeRequest request) {
                    request.destroySession();
                    request.response().end();
                }
            });
        }});

        app.listen(8000);
    }
}