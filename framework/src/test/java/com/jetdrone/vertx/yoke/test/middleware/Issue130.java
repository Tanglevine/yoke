package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.*;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import io.vertx.core.Handler;

import java.util.regex.Pattern;

public class Issue130 extends VertxTestBase {

    @Test
    public void testRegEx() {
        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Router().get(Pattern.compile("^/url/.*"), (request, next) -> request.response().end("OK")));

        new YokeTester(yoke).request(HttpMethod.GET, "/url/http://www.google.com", resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
        await();
    }

    @Test
    public void testRegExEscaped() {
        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Router().get(Pattern.compile("^/url/.*"), (request, next) -> request.response().end("OK")));

        new YokeTester(yoke).request(HttpMethod.GET, "/url/http%3A%2F%2Fwww.google.com", resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
        await();
    }

//    @Test
//    public void testParam() {
//        Yoke yoke = new Yoke(vertx);
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
