package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.CookieParser;
import com.jetdrone.vertx.yoke.middleware.Router;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Test;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.CaseInsensitiveMultiMap;
import org.vertx.testtools.TestVerticle;

import javax.crypto.Mac;

import static org.vertx.testtools.VertxAssert.*;

public class Session extends TestVerticle {

    @Test
    public void testSession() {
        final Yoke yoke = new Yoke(this);
        yoke.secretSecurity("keyboard cat");

        final Mac hmac = yoke.security().getMac("HmacSHA256");
        yoke.use(new CookieParser(hmac));
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Session(hmac));
        yoke.use(new Router() {{
            get("/", (request, next) -> request.response().end());
            get("/new", (request, next) -> {
                request.createSession();
                request.response().end();
            });
            get("/delete", (request, next) -> {
                request.destroySession();
                request.response().end();
            });
        }});

        final YokeTester yokeAssert = new YokeTester(yoke);

        yokeAssert.request("GET", "/", resp -> {
            // start: there is no cookie
            assertEquals(200, resp.getStatusCode());
            String nocookie = resp.headers.get("set-cookie");
            assertNull(nocookie);

            // create session
            yokeAssert.request("GET", "/new", resp1 -> {
                // start: there is a cookie
                assertEquals(200, resp1.getStatusCode());
                final String cookie = resp1.headers.get("set-cookie");
                assertNotNull(cookie);

                // make a new request to / with cookie should return again the same cookie
                MultiMap headers = new CaseInsensitiveMultiMap();
                headers.add("cookie", cookie);

                yokeAssert.request("GET", "/", headers, resp2 -> {
                    // the session should be the same, so no set-cookie
                    assertEquals(200, resp2.getStatusCode());
                    String nocookie1 = resp2.headers.get("set-cookie");
                    assertNull(nocookie1);

                    // end the session
                    MultiMap headers1 = new CaseInsensitiveMultiMap();
                    headers1.add("cookie", cookie);

                    yokeAssert.request("GET", "/delete", headers1, resp3 -> {
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
    }
}
