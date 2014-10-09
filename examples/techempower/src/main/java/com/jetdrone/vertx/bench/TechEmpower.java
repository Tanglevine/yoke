package com.jetdrone.vertx.bench;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.engine.MVELEngine;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import io.vertx.core.AbstractVerticle;
import org.jetbrains.annotations.NotNull;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Handler;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TechEmpower extends AbstractVerticle {

    // utilities
    private static JsonObject buildQuery(String action, String collection, int id) {
        return new JsonObject()
                .putString("action", action)
                .putString("collection", collection)
                .putObject("matcher", new JsonObject().putNumber("id", id));
    }

    private static JsonObject buildQuery(String action, String collection) {
        return new JsonObject()
                .putString("action", action)
                .putString("collection", collection)
                .putObject("matcher", new JsonObject());
    }

    private static JsonObject buildUpdate(String collection, int id, int randomNumber) {
        return new JsonObject()
                .putString("collection", collection)
                .putString("action", "update")
                .putObject("criteria", new JsonObject().putNumber("id", id))
                .putObject("objNew", new JsonObject().
                        putObject("$set", new JsonObject().putNumber("randomNumber", randomNumber)));

    }

    private static int parseRequestParam(YokeRequest request, String param) {
        int value = 1;
        try {
            value = Integer.parseInt(request.params().get(param));
            // Bounds check.
            if (value > 500) {
                value = 500;
            }
            if (value < 1) {
                value = 1;
            }
        } catch (NumberFormatException nfexc) {
            // do nothing
        }
        return value;
    }

    @Override
    public void start() {

        final EventBus eb = vertx.eventBus();
        final String address = "bench.mongodb";

        JsonObject dbConfig = new JsonObject()
                .putString("address", address)
                .putString("db_name", "hello_world")
                .putString("host", "localhost");

        // deploy mongo module
        container.deployModule("io.vertx~mod-mongo-persistor~2.0.0-final", dbConfig);

        // create the yoke app
        new Yoke(this)
                // register the MVEL engine
                .engine("mvel", new MVELEngine("views"))

                // Test 1: JSON serialization
                .use("/json", new Middleware() {
                    @Override
                    public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                        // For each request, an object mapping the key message to Hello, World! must be instantiated.
                        request.response().end(new JsonObject().putString("message", "Hello, World!"));
                    }
                })
                // Test 2: db
                .use("/db", new Middleware() {
                    @Override
                    public void handle(@NotNull final YokeRequest request, @NotNull final Handler<Object> next) {

                        final Random random = ThreadLocalRandom.current();

                        eb.send(address, buildQuery("findone", "world", random.nextInt(10000) + 1), new Handler<Message<JsonObject>>() {
                            @Override
                            public void handle(Message<JsonObject> message) {
                                // get the body
                                final JsonObject body = message.body();

                                if ("ok".equals(body.getString("status"))) {
                                    // json -> string serialization
                                    request.response().end(body.getObject("result"));
                                    return;
                                }

                                next.handle(body.getString("message"));
                            }
                        });
                    }
                })
                // Test 3: queries
                .use("/queries", new Middleware() {
                    @Override
                    public void handle(@NotNull final YokeRequest request, @NotNull Handler<Object> next) {

                        final Random random = ThreadLocalRandom.current();

                        // Get the count of queries to run.
                        final int count = parseRequestParam(request, "queries");

                        final Handler<Message<JsonObject>> dbh = new Handler<Message<JsonObject>>() {
                            // how many messages have this handler received
                            int received = 0;
                            // keeps the received messages
                            JsonArray result = new JsonArray();

                            @Override
                            public void handle(Message<JsonObject> message) {
                                // increase the counter
                                received++;

                                // get the body
                                final JsonObject body = message.body();

                                if ("ok".equals(body.getString("status"))) {
                                    // json -> string serialization
                                    result.add(body.getObject("result"));
                                }

                                // end condition
                                if (received == count) {
                                    request.response().end(result);
                                }
                            }
                        };

                        for (int i = 0; i < count; i++) {
                            eb.send(address, buildQuery("findone", "world", random.nextInt(10000) + 1), dbh);
                        }
                    }
                })
                // Test 4: fortune
                .use("/fortunes", new Middleware() {
                    @Override
                    public void handle(@NotNull final YokeRequest request, @NotNull final Handler<Object> next) {

                        final List<JsonObject> results = new ArrayList<>();

                        eb.send(address, buildQuery("find", "fortune"), new Handler<Message<JsonObject>>() {
                            @Override
                            public void handle(Message<JsonObject> reply) {
                                String status = reply.body().getString("status");

                                if (status != null) {
                                    if ("ok".equalsIgnoreCase(status)) {
                                        JsonArray itResult = reply.body().getArray("results");
                                        for (Object o : itResult) {
                                            results.add((JsonObject) o);
                                        }
                                        // end condition
                                        results.add(new JsonObject().putNumber("id", 0).putString("message", "Additional fortune added at request time."));
                                        // sort ASC
                                        Collections.sort(results, new Comparator<JsonObject>() {
                                            @Override
                                            public int compare(JsonObject o1, JsonObject o2) {
                                                return o1.getNumber("id").intValue() - o2.getNumber("id").intValue();
                                            }
                                        });
                                        // render
                                        request.put("fortunes", results);
                                        request.response().render("fortunes.mvel", next);
                                        return;
                                    }
                                    if ("more-exist".equalsIgnoreCase(status)) {
                                        JsonArray itResult = reply.body().getArray("results");
                                        for (Object o : itResult) {
                                            results.add((JsonObject) o);
                                        }
                                        // reply asking for more
                                        reply.reply(this);
                                        return;
                                    }
                                }
                                next.handle("error");
                            }
                        });
                    }
                })
                // Test 5: updates
                .use("/updates", new Middleware() {
                    @Override
                    public void handle(@NotNull final YokeRequest request, @NotNull final Handler<Object> next) {

                        final Random random = ThreadLocalRandom.current();

                        // Get the count of queries to run.
                        final int count = parseRequestParam(request, "queries");

                        final Handler<Message<JsonObject>> dbh = new Handler<Message<JsonObject>>() {
                            // how many messages have this handler received
                            int received = 0;
                            // keeps the received messages
                            JsonArray worlds = new JsonArray();

                            @Override
                            public void handle(Message<JsonObject> message) {

                                // get the body
                                final JsonObject body = message.body();
                                JsonObject world;

                                if ("ok".equals(body.getString("status"))) {
                                    world = new JsonObject()
                                            .putNumber("id", received)
                                            .putNumber("randomNumber", body.getObject("result").getNumber("randomNumber"));
                                    worlds.add(world);

                                    world.putNumber("randomNumber", random.nextInt(10000) + 1);
                                    eb.send(address, buildUpdate("world", world.getInteger("id"), world.getInteger("randomNumber")));
                                }

                                // increase the counter
                                received++;

                                // end condition
                                if (received == count) {
                                    request.response().end(worlds);
                                }
                            }
                        };

                        for (int i = 0; i < count; i++) {
                            eb.send(address, buildQuery("findone", "world", random.nextInt(10000) + 1), dbh);
                        }
                    }
                })
                // Test 6: plain text
                .use("/plaintext", new Middleware() {
                    @Override
                    public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                        request.response().setContentType("text/plain");
                        // Write plaintext "Hello, World!" to the response.
                        request.response().end("Hello, World!");
                    }
                })
                // listen on port 8080
                .listen(8080);
    }
}
