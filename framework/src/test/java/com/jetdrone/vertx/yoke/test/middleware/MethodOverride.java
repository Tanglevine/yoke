package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

public class MethodOverride extends VertxTestBase {

    @Test
    public void testOverride() {

        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.MethodOverride());
        yoke.use(request -> {
            assertEquals("DELETE", request.method());
            request.response().end();
        });

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("x-http-setMethod-override", "DELETE");

        new YokeTester(yoke).request(HttpMethod.GET, "/upload", headers, resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
    }

    @Test
    public void testOverrideUrlPost() {

        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BodyParser());
        yoke.use(new com.jetdrone.vertx.yoke.middleware.MethodOverride());
        yoke.use(request -> {
            assertEquals("DELETE", request.method());
            request.response().end();
        });

        Buffer body = Buffer.buffer("_method=delete");

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("content-type", "application/x-www-form-urlencoded");
        headers.add("content-length", Integer.toString(body.length()));

        new YokeTester(yoke).request(HttpMethod.POST, "/upload", headers, body, resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
    }

    @Test
    public void testOverrideJsonPost() {

        final JsonObject json = new JsonObject().putString("_method", "delete");

        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BodyParser());
        yoke.use(new com.jetdrone.vertx.yoke.middleware.MethodOverride());
        yoke.use(request -> {
            assertEquals("DELETE", request.method());
            request.response().end();
        });

        Buffer body = Buffer.buffer(json.encode());

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("content-type", "application/json");
        headers.add("content-length", Integer.toString(body.length()));

        new YokeTester(yoke).request(HttpMethod.POST, "/upload", headers, body, resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
        await();
    }
}
