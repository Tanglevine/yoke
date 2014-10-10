package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.AuthHandler;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

public class BasicAuth extends VertxTestBase {

    @Test
    public void testBasicAuth() {
        final Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.BasicAuth("Aladdin", "open sesame"));
        yoke.use((request, next) -> request.response().end());

        final YokeTester yokeAssert = new YokeTester(yoke);

        // first time is forbidden
        yokeAssert.request(HttpMethod.GET, "/", resp -> {
            assertEquals(401, resp.getStatusCode());
            assertNotNull(resp.headers.get("www-authenticate"));

            // second time send the authorization header
            MultiMap headers = new CaseInsensitiveHeaders();
            headers.add("authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");

            yokeAssert.request(HttpMethod.GET, "/", headers, resp1 -> {
                assertEquals(200, resp1.getStatusCode());
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testEmptyPassword() {
        final Yoke yoke = new Yoke(vertx);
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
        yokeAssert.request(HttpMethod.GET, "/", resp -> {
            assertEquals(401, resp.getStatusCode());
            assertNotNull(resp.headers.get("www-authenticate"));

            // second time send the authorization header
            MultiMap headers = new CaseInsensitiveHeaders();
            headers.add("authorization", "Basic QWxhZGRpbjo=");

            yokeAssert.request(HttpMethod.GET, "/", headers, resp1 -> {
                assertEquals(200, resp1.getStatusCode());
                testComplete();
            });
        });
        await();
    }
}
