package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Vhost;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.Response;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Test;
import org.vertx.java.core.http.CaseInsensitiveMultiMap;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class VHost extends TestVerticle {

    @Test
    public void testLimit() {
        Yoke yoke = new Yoke(this);
        yoke.use(new Vhost("*.com", new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
                request.response().end();
                testComplete();
            }
        }));
        yoke.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                request.response().end();
                fail();
            }
        });

        MultiMap headers = new CaseInsensitiveMultiMap();
        headers.add("host", "www.mycorp.com");

        new YokeTester(yoke).request("GET", "/", headers, resp -> {
        });
    }
}
