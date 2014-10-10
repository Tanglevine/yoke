package com.jetdrone.vertx.yoke.engine;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import io.vertx.core.Handler;

public class ThymeleafEngineTest extends VertxTestBase {

    @Test
    public void testEngine() {
        Yoke yoke = new Yoke(vertx);
        yoke.engine("html", new ThymeleafEngine("target/test-classes/views"));
        yoke.use((request, next) -> {
            request.put("home.welcome", "Hi there!");
            request.response().render("template.html");
        });

        new YokeTester(yoke).request(HttpMethod.GET, "/", resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
        await();
    }
}
