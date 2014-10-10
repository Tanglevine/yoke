/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.store;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;

/** #SharedDataSessionStore */
public class LocalDataSessionStore implements SessionStore {

    private final LocalMap<String, String> storage;

    public LocalDataSessionStore(Vertx vertx, String name) {
        storage = vertx.sharedData().getLocalMap(name);
    }

    @Override
    public void get(String sid, Handler<JsonObject> callback) {
        String sess = storage.get(sid);

        if (sess == null) {
            callback.handle(null);
            return;
        }

        callback.handle(new JsonObject(sess));
    }

    @Override
    public void set(String sid, JsonObject sess, Handler<Object> callback) {
        storage.put(sid, sess.encode());
        callback.handle(null);
    }

    @Override
    public void destroy(String sid, Handler<Object> callback) {
        storage.remove(sid);
        callback.handle(null);
    }

    @Override
    public void clear(Handler<Object> callback) {
        storage.clear();
        callback.handle(null);
    }
}
