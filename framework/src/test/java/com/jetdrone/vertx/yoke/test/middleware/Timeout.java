package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.Handler;

public class Timeout extends VertxTestBase {

    @Test
    public void testTimeout() {
        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Timeout(10));
        yoke.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                // noop to so the response would never end
            }
        });

        new YokeTester(yoke).request("GET", "/", resp -> {
            assertEquals(408, resp.getStatusCode());
            testComplete();
        });
    }
}
