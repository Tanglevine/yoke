package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Test;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.testComplete;

public class Timeout extends TestVerticle {

    @Test
    public void testTimeout() {
        Yoke yoke = new Yoke(this);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Timeout(10));
        yoke.use((request, next) -> {
            // noop to so the response would never end
        });

        new YokeTester(yoke).request("GET", "/", resp -> {
            assertEquals(408, resp.getStatusCode());
            testComplete();
        });
    }
}
