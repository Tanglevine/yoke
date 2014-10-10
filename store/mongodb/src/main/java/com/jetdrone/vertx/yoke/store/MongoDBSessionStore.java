/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.store;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import org.jetbrains.annotations.NotNull;

/**
 * # MongoDBSessionStore
 * <p>
 * mongo db collection *MUST* have a TTL index on updatedAt
 * <p>
 * db.collection.ensureIndex({updatedAt: 1}, {expireAfterSeconds: 3600});
 */
public class MongoDBSessionStore implements SessionStore {

    private final String collection;
    private final MongoService mongo;

    public MongoDBSessionStore(@NotNull MongoService mongo, @NotNull String collection) {
        this.mongo = mongo;
        this.collection = collection;
    }

    @Override
    public void get(final String sid, final Handler<JsonObject> next) {
        mongo.findOne(collection, new JsonObject().putString("id", sid), null, new Handler<AsyncResult<JsonObject>>() {
            @Override
            public void handle(AsyncResult<JsonObject> reply) {
                if (reply.succeeded()) {
                    JsonObject value = reply.result();
                    if (value == null) {
                        next.handle(null);
                        return;
                    }

                    // clean mongodb specific fields
                    value.removeField("_id");
                    value.removeField("updatedAt");

                    next.handle(value);
                } else {
                    next.handle(null);
                }
            }
        });
    }

    @Override
    public void set(final String sid, JsonObject sess, final Handler<Object> next) {
        // force the session id
        sess.putString("id", sid);
        // updated at
        sess.putObject("updatedAt", new JsonObject().putNumber("$date", System.currentTimeMillis()));

        mongo.update(collection, new JsonObject().putString("id", sid), sess, "NORMAL", true, false, new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> reply) {
                if (reply.succeeded()) {
                    next.handle(null);
                } else {
                    next.handle(reply.cause());
                }
            }
        });
    }

    @Override
    public void destroy(String sid, final Handler<Object> next) {
        mongo.delete(collection, new JsonObject().putString("id", sid), "NORMAL", new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> reply) {
                if (reply.succeeded()) {
                    next.handle(null);
                } else {
                    next.handle(reply.cause());
                }
            }
        });
    }

    @Override
    public void clear(final Handler<Object> next) {
        mongo.delete(collection, new JsonObject(), "NORMAL", new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> reply) {
                if (reply.succeeded()) {
                    next.handle(null);
                } else {
                    next.handle(reply.cause());
                }
            }
        });
    }
}
