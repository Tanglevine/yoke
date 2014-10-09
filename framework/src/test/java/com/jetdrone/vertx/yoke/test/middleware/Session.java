package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.CookieParser;
import com.jetdrone.vertx.yoke.middleware.Router;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;

import javax.crypto.Mac;

public class Session extends VertxTestBase {

    @Test
    public void testSession() {
        final Yoke yoke = new Yoke(vertx);
        yoke.secretSecurity("keyboard cat");

        final Mac hmac = yoke.security().getMac("HmacSHA256");
        yoke.use(new CookieParser(hmac));
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Session(hmac));
        yoke.use(new Router() {{
            get("/", new Handler<YokeRequest>() {
                @Override
                public void handle(YokeRequest request) {
                    request.response().end();
                }
            });
            get("/new", new Handler<YokeRequest>() {
                @Override
                public void handle(YokeRequest request) {
                    request.createSession();
                    request.response().end();
                }
            });
            get("/delete", new Handler<YokeRequest>() {
                @Override
                public void handle(YokeRequest request) {
                    request.destroySession();
                    request.response().end();
                }
            });
        }});

        final YokeTester yokeAssert = new YokeTester(yoke);

        yokeAssert.request(HttpMethod.GET, "/", resp -> {
            // start: there is no cookie
            assertEquals(200, resp.getStatusCode());
            String nocookie = resp.headers.get("set-cookie");
            assertNull(nocookie);

            // create session
            yokeAssert.request(HttpMethod.GET, "/new", resp1 -> {
                // start: there is a cookie
                assertEquals(200, resp1.getStatusCode());
                final String cookie = resp1.headers.get("set-cookie");
                assertNotNull(cookie);

                // make a new request to / with cookie should return again the same cookie
                MultiMap headers = new CaseInsensitiveHeaders();
                headers.add("cookie", cookie);

                yokeAssert.request(HttpMethod.GET, "/", headers, resp2 -> {
                    // the session should be the same, so no set-cookie
                    assertEquals(200, resp2.getStatusCode());
                    String nocookie1 = resp2.headers.get("set-cookie");
                    assertNull(nocookie1);

                    // end the session
                    MultiMap headers1 = new CaseInsensitiveHeaders();
                    headers1.add("cookie", cookie);

                    yokeAssert.request(HttpMethod.GET, "/delete", headers1, resp3 -> {
                        // there should be a set-cookie with maxAge 0
                        assertEquals(200, resp3.getStatusCode());
                        String cookie1 = resp3.headers.get("set-cookie");
                        assertNotNull(cookie1);

                        assertTrue(cookie1.startsWith("yoke.sess=;"));
                        testComplete();
                    });
                });
            });
        });
        await();
    }
}
