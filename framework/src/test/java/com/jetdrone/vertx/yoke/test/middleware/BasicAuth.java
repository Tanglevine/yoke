package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Test;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.CaseInsensitiveMultiMap;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class BasicAuth extends TestVerticle {

    @Test
    public void testBasicAuth() {
        final Yoke yoke = new Yoke(this);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BasicAuth("Aladdin", "open sesame"));
        yoke.use((request, next) -> request.response().end());

        final YokeTester yokeAssert = new YokeTester(yoke);

        // first time is forbidden
        yokeAssert.request("GET", "/", resp -> {
            assertEquals(401, resp.getStatusCode());
            assertNotNull(resp.headers.get("www-authenticate"));

            // second time send the authorization header
            MultiMap headers = new CaseInsensitiveMultiMap();
            headers.add("authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");

            yokeAssert.request("GET", "/", headers, resp1 -> {
                assertEquals(200, resp1.getStatusCode());
                testComplete();
            });
        });
    }

    @Test
    public void testEmptyPassword() {
        final Yoke yoke = new Yoke(this);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BasicAuth((username, password, result) -> {
            boolean success = username.equals("Aladdin") && password == null;
            if (success) {
                result.handle(new JsonObject().putString("username", username));
            } else {
                result.handle(null);
            }
        }));

        yoke.use((request, next) -> request.response().end());

        final YokeTester yokeAssert = new YokeTester(yoke);

        // first time is forbidden
        yokeAssert.request("GET", "/", resp -> {
            assertEquals(401, resp.getStatusCode());
            assertNotNull(resp.headers.get("www-authenticate"));

            // second time send the authorization header
            MultiMap headers = new CaseInsensitiveMultiMap();
            headers.add("authorization", "Basic QWxhZGRpbjo=");

            yokeAssert.request("GET", "/", headers, resp1 -> {
                assertEquals(200, resp1.getStatusCode());
                testComplete();
            });
        });
    }
}
