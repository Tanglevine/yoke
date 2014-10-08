package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.test.core.VertxTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;

public class YokeRequestTest extends VertxTestBase {

    @Test
    public void testAccept() {
        final Yoke yoke = new Yoke(vertx);
        yoke.use(new Middleware() {
            @Override
            public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                assertNotNull(request.accepts("application/json"));
                testComplete();
            }
        });

        // make a new request to / with cookie should return again the same cookie
        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        new YokeTester(yoke).request("GET", "/", headers, null);
    }

    @Test
    public void testNormalizedPath() {
        final Yoke yoke = new Yoke(vertx);
        yoke.use(new Middleware() {
            @Override
            public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                assertEquals("/pom.xml", request.normalizedPath());
                testComplete();
            }
        });

        new YokeTester(yoke).request("GET", "/./me/../pom.xml", null);
    }

    @Test
    public void testNormalizedPath2() {
        final Yoke yoke = new Yoke(vertx);
        yoke.use(new Middleware() {
            @Override
            public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                assertEquals("/", request.normalizedPath());
                testComplete();
            }
        });

        new YokeTester(yoke).request("GET", "/", null);
    }

    @Test
    public void testNormalizedPath3() {
        final Yoke yoke = new Yoke(vertx);
        yoke.use(new Middleware() {
            @Override
            public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                assertNull(request.normalizedPath());
                testComplete();
            }
        });

        new YokeTester(yoke).request("GET", "/%2e%2e%2f", null);
    }

    @Test
    public void testNormalizedPath4() {
        final Yoke yoke = new Yoke(vertx);
        yoke.use(new Middleware() {
            @Override
            public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                assertNull(request.normalizedPath());
                testComplete();
            }
        });

        new YokeTester(yoke).request("GET", "/%2e%2e/", null);
    }

    @Test
    public void testNormalizedPath5() {
        final Yoke yoke = new Yoke(vertx);
        yoke.use(new Middleware() {
            @Override
            public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                assertNull(request.normalizedPath());
                testComplete();
            }
        });

        new YokeTester(yoke).request("GET", "/..%2f", null);
    }
}
