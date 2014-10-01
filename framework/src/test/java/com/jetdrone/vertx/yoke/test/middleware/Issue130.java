package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.test.Response;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.testtools.TestVerticle;

import java.util.regex.Pattern;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.testComplete;

public class Issue130 extends TestVerticle {

    @Test
    public void testRegEx() {
        Yoke yoke = new Yoke(this);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Router().get(Pattern.compile("^/url/.*"), (request, next) -> request.response().end("OK")));

        new YokeTester(yoke).request("GET", "/url/http://www.google.com", new Handler<Response>() {
            @Override
            public void handle(Response resp) {
                assertEquals(200, resp.getStatusCode());
                testComplete();
            }
        });
    }

    @Test
    public void testRegExEscaped() {
        Yoke yoke = new Yoke(this);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Router().get(Pattern.compile("^/url/.*"), (request, next) -> request.response().end("OK")));

        new YokeTester(yoke).request("GET", "/url/http%3A%2F%2Fwww.google.com", resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
    }

//    @Test
//    public void testParam() {
//        Yoke yoke = new Yoke(this);
//        yoke.use(new com.jetdrone.vertx.yoke.middleware.Router().get("/url/:url", new Middleware() {
//            @Override
//            public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
//                request.response().end(request.getParameter("url"));
//            }
//        }));
//
//        new YokeTester(yoke).request("GET", "/url/http%3A%2F%2Fwww.google.com", new Handler<Response>() {
//            @Override
//            public void handle(Response resp) {
//                assertEquals(200, resp.getStatusCode());
//                testComplete();
//            }
//        });
//    }
}
