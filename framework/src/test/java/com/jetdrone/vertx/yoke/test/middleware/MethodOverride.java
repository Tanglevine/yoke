package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Test;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.CaseInsensitiveMultiMap;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.testComplete;

public class MethodOverride extends TestVerticle {

    @Test
    public void testOverride() {

        Yoke yoke = new Yoke(this);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.MethodOverride());
        yoke.use((request, next) -> {
            assertEquals("DELETE", request.method());
            request.response().end();
        });

        MultiMap headers = new CaseInsensitiveMultiMap();
        headers.add("x-http-setMethod-override", "DELETE");

        new YokeTester(yoke).request("GET", "/upload", headers, resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
    }

    @Test
    public void testOverrideUrlPost() {

        Yoke yoke = new Yoke(this);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BodyParser());
        yoke.use(new com.jetdrone.vertx.yoke.middleware.MethodOverride());
        yoke.use((request, next) -> {
            assertEquals("DELETE", request.method());
            request.response().end();
        });

        Buffer body = new Buffer("_method=delete");

        MultiMap headers = new CaseInsensitiveMultiMap();
        headers.add("content-type", "application/x-www-form-urlencoded");
        headers.add("content-length", Integer.toString(body.length()));

        new YokeTester(yoke).request("POST", "/upload", headers, body, resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
    }

    @Test
    public void testOverrideJsonPost() {

        final JsonObject json = new JsonObject().putString("_method", "delete");

        Yoke yoke = new Yoke(this);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BodyParser());
        yoke.use(new com.jetdrone.vertx.yoke.middleware.MethodOverride());
        yoke.use((request, next) -> {
            assertEquals("DELETE", request.method());
            request.response().end();
        });

        Buffer body = new Buffer(json.encode());

        MultiMap headers = new CaseInsensitiveMultiMap();
        headers.add("content-type", "application/json");
        headers.add("content-length", Integer.toString(body.length()));

        new YokeTester(yoke).request("POST", "/upload", headers, body, resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
    }
}
