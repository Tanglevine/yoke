package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Test;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class ResponseTime extends TestVerticle {

    @Test
    public void testResponseTime() {
        Yoke yoke = new Yoke(this);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.ResponseTime());
        yoke.use((request, next) -> request.response().end());

        new YokeTester(yoke).request("GET", "/", resp -> {
            assertEquals(200, resp.getStatusCode());
            assertNotNull(resp.headers().get("x-response-time"));
            testComplete();
        });
    }
}
