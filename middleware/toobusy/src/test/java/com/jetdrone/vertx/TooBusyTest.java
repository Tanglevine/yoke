package com.jetdrone.vertx;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.TooBusy;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.Test;
import io.vertx.core.Handler;

import java.security.SecureRandom;

public class TooBusyTest extends VertxTestBase {

    @Test
    @Ignore("vert.x3 does have some safety checks against blocking the main loop")
    public void testIsTooBusy() throws Exception {

        final Yoke yoke = new Yoke(vertx);
        final TooBusy tooBusy = new TooBusy();
        yoke.use(tooBusy);
        yoke.use(new Middleware() {
            double cnt = 0;
            final SecureRandom rand = new SecureRandom();

            @Override
            public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                for (int i = 0; i < 100000; i++) {
                    cnt += rand.nextDouble();
                }

                request.response().end();
            }
        });

        final YokeTester tester = new YokeTester(yoke, false);

        vertx.setPeriodic(100, new Handler<Long>() {
            int i = 0;
            int some200 = 0;
            int some503 = 0;

            @Override
            public void handle(Long event) {
                tester.request(HttpMethod.GET, "/", response -> {
                    if (response.getStatusCode() == 200) {
                        some200++;
                    }
                    if (response.getStatusCode() == 503) {
                        some503++;
                    }
                });

                if (++i == 100) {
                    System.out.println("[200]: " + some200 + " [503]: " + some503);
                    assertTrue(some200 > 0);
                    assertTrue(some503 > 0);
                    testComplete();
                }
            }
        });
        await();
    }
}