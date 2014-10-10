package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.Handler;

public class ResponseTime extends VertxTestBase {

    @Test
    public void testResponseTime() {
        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.ResponseTime());
        yoke.use((request, next) -> request.response().end());

        new YokeTester(yoke).request(HttpMethod.GET, "/", resp -> {
            assertEquals(200, resp.getStatusCode());
            assertNotNull(resp.headers().get("x-response-time"));
            testComplete();
        });
        await();
    }
}
