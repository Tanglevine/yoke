package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.middleware.Limit;
import com.jetdrone.vertx.yoke.test.Response;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.netty.handler.codec.http.HttpHeaders;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

public class BodyParser extends VertxTestBase {

    @Test
    public void testJsonBodyParser() {

        final JsonObject json = new JsonObject().putString("key", "value");

        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BodyParser());
        yoke.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                assertNotNull(request.body());
                assertEquals(((JsonObject) request.body()).encode(), json.encode());
                request.response().end();
            }
        });

        Buffer body = Buffer.buffer(json.encode());

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("content-type", "application/json");
        headers.add("content-length", Integer.toString(body.length()));

        new YokeTester(yoke).request(HttpMethod.POST, "/upload", headers, body, new Handler<Response>() {
            @Override
            public void handle(Response resp) {
                assertEquals(200, resp.getStatusCode());
                assertNotNull(resp.body);
                testComplete();
            }
        });
        await();
    }

    @Test
    public void testMapBodyParser() {

        Yoke yoke = new Yoke(vertx);
        yoke.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                MultiMap body = request.formAttributes();
                assertEquals("value", body.get("param"));
                request.response().end();
            }
        });

        Buffer body = Buffer.buffer("param=value");

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("content-type", "application/x-www-form-urlencoded");
        headers.add("content-length", Integer.toString(body.length()));

        new YokeTester(yoke).request(HttpMethod.POST, "/upload", headers, body, new Handler<Response>() {
            @Override
            public void handle(Response resp) {
                assertEquals(200, resp.getStatusCode());
                assertNotNull(resp.body);
                testComplete();
            }
        });
        await();
    }

    @Test
    public void testTextBodyParser() {

        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BodyParser());
        yoke.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                Buffer body = request.body();
                assertEquals("hello-world", body.toString());
                request.response().end();
            }
        });

        Buffer body = Buffer.buffer("hello-world");

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("content-length", Integer.toString(body.length()));

        new YokeTester(yoke).request(HttpMethod.POST, "/upload", headers, body, new Handler<Response>() {
            @Override
            public void handle(Response resp) {
                assertEquals(200, resp.getStatusCode());
                assertNotNull(resp.body);
                testComplete();
            }
        });
        await();
    }

    @Test
    public void testBodyParserWithEmptyBody() {

        Yoke yoke = new Yoke(vertx);
        yoke.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                request.response().end();
            }
        });

        new YokeTester(yoke).request(HttpMethod.DELETE, "/upload", resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
        await();
    }

    @Test
    public void testJsonBodyLengthLimit() {

        Yoke yoke = new Yoke(vertx);
        yoke.use(new Limit(5L));
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BodyParser());
        yoke.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                fail("Body should have been too long");
            }
        });

        Buffer body = Buffer.buffer("[1,2,3,4,5]");

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("content-type", "application/json");
        headers.add("transfer-encoding", "chunked");

        new YokeTester(yoke).request(HttpMethod.POST, "/upload", headers, body, resp -> {
            assertEquals(413, resp.getStatusCode());
            testComplete();
        });
        await();
    }

    @Test
    public void testTextBodyLengthLimit() {

        Yoke yoke = new Yoke(vertx);
        yoke.use(new Limit(5L));
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BodyParser());
        yoke.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                fail("Body should have been too long");
            }
        });

        Buffer body = Buffer.buffer("hello world");

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("content-type", "plain/text");
        headers.add("transfer-encoding", "chunked");

        new YokeTester(yoke).request(HttpMethod.POST, "/upload", headers, body, resp -> {
            assertEquals(413, resp.getStatusCode());
            testComplete();
        });
        await();
    }

    @Test
    public void testFormEncodedBodyLengthLimit() {

        Yoke yoke = new Yoke(vertx);
        yoke.use(new Limit(5L));
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BodyParser());
        yoke.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                fail("Body should have been too long");
            }
        });

        Buffer body = Buffer.buffer("hello=world");

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("content-type", HttpHeaders.Values.APPLICATION_X_WWW_FORM_URLENCODED);
        headers.add("transfer-encoding", "chunked");

        new YokeTester(yoke).request(HttpMethod.POST, "/upload", headers, body, resp -> {
            assertEquals(413, resp.getStatusCode());
            testComplete();
        });
        await();
    }

    @Test
    public void testDeleteContentLengthZeroWithNoBody() {

        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BodyParser());
        yoke.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                request.response().setStatusCode(204);
                request.response().end("");
            }
        });

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Content-Length", "0");

        new YokeTester(yoke).request(HttpMethod.DELETE, "/delete", headers, resp -> {
            assertEquals(204, resp.getStatusCode());
            testComplete();
        });
        await();
    }
}
