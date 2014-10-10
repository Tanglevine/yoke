/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.store;

import com.jetdrone.vertx.yoke.util.AsyncIterator;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisService;
import org.jetbrains.annotations.NotNull;

/** # RedisSessionStore */
public class RedisSessionStore implements SessionStore {

    private final int ttl;
    private final String prefix;
    private final RedisService redis;

    public RedisSessionStore(@NotNull RedisService redis, String prefix, Integer ttl) {
        this.redis = redis;
        this.prefix = prefix;
        this.ttl = ttl;
    }

    public RedisSessionStore(@NotNull RedisService redis, String prefix) {
        this(redis, prefix, 86400);
    }

    @Override
    public void get(String sid, final Handler<JsonObject> callback) {
        sid = this.prefix + sid;

        redis.get(new JsonArray().add(sid), new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> reply) {
                if (reply.succeeded()) {
                    String value = reply.result();
                    if (value == null || "".equals(value)) {
                        callback.handle(null);
                        return;
                    }
                    callback.handle(new JsonObject(value));
                } else {
                    callback.handle(null);
                }
            }
        });
    }

    @Override
    public void set(String sid, JsonObject sess, final Handler<Object> callback) {
        sid = prefix + sid;

        Integer maxAge = null;

        JsonObject obj = sess.getObject("cookie");
        if (obj != null) {
            maxAge = obj.getInteger("maxAge");
        }

        String session = sess.encode();
        int ttl;

        if (maxAge != null) {
            ttl = maxAge / 1000;
        } else {
            ttl = this.ttl;
        }

        redis.setex(new JsonArray().add(sid).add(ttl).add(session), new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> reply) {
                if (reply.succeeded()) {
                    callback.handle(null);
                } else {
                    callback.handle(reply.cause());
                }
            }
        });
    }

    @Override
    public void destroy(String sid, final Handler<Object> callback) {
        sid = this.prefix + sid;

        redis.del(new JsonArray().add(sid), new Handler<AsyncResult<Long>>() {
            @Override
            public void handle(AsyncResult<Long> reply) {
                if (reply.succeeded()) {
                    callback.handle(null);
                } else {
                    callback.handle(reply.cause());
                }
            }
        });
    }

    @Override
    public void clear(final Handler<Object> next) {

        redis.keys(new JsonArray().add(prefix + "*"), new Handler<AsyncResult<JsonArray>>() {
            @Override
            public void handle(AsyncResult<JsonArray> reply) {
                if (reply.failed()) {
                    next.handle(reply.cause());
                } else {
                    new AsyncIterator<Object>(reply.result()) {
                        @Override
                        public void handle(Object key) {
                            if (hasNext()) {
                                redis.del(new JsonArray().add(key), new Handler<AsyncResult<Long>>() {
                                    @Override
                                    public void handle(AsyncResult<Long> reply) {
                                        if (reply.succeeded()) {
                                            next();
                                        } else {
                                            next.handle(reply.cause());
                                        }
                                    }
                                });
                            } else {
                                next.handle(null);
                            }
                        }
                    };
                }
            }
        });
    }
}
