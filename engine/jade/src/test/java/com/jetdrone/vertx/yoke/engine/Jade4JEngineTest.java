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

public class Jade4JEngineTest extends VertxTestBase {

    @Test
    public void testEngine() {
        Yoke yoke = new Yoke(vertx);
        yoke.engine("jade", new Jade4JEngine("target/test-classes/views"));
        yoke.use((request, next) -> request.response().render("template.jade"));

        new YokeTester(yoke).request(HttpMethod.GET, "/", resp -> {
            assertEquals(200, resp.getStatusCode());
            assertEquals("<!DOCTYPE html><html><head></head><body></body></html>", resp.body.toString());
            testComplete();
        });
        await();
    }

    @Test
    public void testEngine2() {
        Yoke yoke = new Yoke(vertx);
        yoke.engine("jade", new Jade4JEngine("target/test-classes/views"));
        yoke.use((request, next) -> {
            request.put("pageName", "Vert.X Test");
            request.response().render("template2.jade");
        });

        new YokeTester(yoke).request(HttpMethod.GET, "/", resp -> {
            assertEquals(200, resp.getStatusCode());
//                assertEquals("<!DOCTYPE html><html><head><title>Vert.X Test</title><script src=\"static/sockjs-min-0.3.4.js\" type=\"text/javascript\"></script><script src=\"static/vertxbus.js\" type=\"text/javascript\"></script><script src=\"static/main.js\" type=\"text/javascript\"></script><link rel=\"stylesheet\" type=\"text/css\" href=\"static/main.css\"></head><body><h1>Vert.X Test</h1></body></html>", resp.body.toString());
            testComplete();
        });
        await();
    }
}
