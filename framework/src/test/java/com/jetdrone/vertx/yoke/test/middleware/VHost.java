package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Vhost;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Test;
import org.vertx.java.core.http.CaseInsensitiveMultiMap;
import org.vertx.java.core.MultiMap;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class VHost extends TestVerticle {

    @Test
    public void testLimit() {
        Yoke yoke = new Yoke(this);
        yoke.use(new Vhost("*.com", request -> {
            request.response().end();
            testComplete();
        }));
        yoke.use((request, next) -> {
            request.response().end();
            fail();
        });

        MultiMap headers = new CaseInsensitiveMultiMap();
        headers.add("host", "www.mycorp.com");

        new YokeTester(yoke).request("GET", "/", headers, resp -> {
        });
    }
}
