package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;

import java.util.Locale;

public class RequestFunctions extends VertxTestBase {

    @Test
    public void testAccepts() {
        Yoke yoke = new Yoke(vertx);
        yoke.use((request, next) -> request.response().end(request.accepts("text")));

        // second time send the authorization header
        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("accept", "text/plain; q=0.5, application/json, text/html; q=0.8, text/xml");
        // expected order is:
        // application/json
        // text/xml
        // text/html
        // text/plain

        new YokeTester(yoke).request(HttpMethod.GET, "/", headers, resp -> {
            assertEquals(200, resp.getStatusCode());
            assertEquals(resp.body.toString(), "text/xml");
            testComplete();
        });
        await();
    }

    @Test
    public void testIp() {
        Yoke yoke = new Yoke(vertx);
        yoke.use((request, next) -> {
            assertEquals("123.456.123.456", request.ip());
            testComplete();
        });

        // second time send the authorization header
        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("x-forward-for", "123.456.123.456, 111.111.11.11");

        new YokeTester(yoke).request(HttpMethod.GET, "/", headers, null);
        await();
    }

    @Test
    public void testLocale() {
        Yoke yoke = new Yoke(vertx);
        yoke.use((request, next) -> {
            assertEquals(new Locale("da", "dk"), request.locale());
            testComplete();
        });

        // second time send the authorization header
        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("Accept-Language", "en-gb;q=0.8, en;q=0.7, da_DK;q=0.9");

        new YokeTester(yoke).request(HttpMethod.GET, "/", headers, null);
        await();
    }

    @Test
    public void testLocale2() {
        Yoke yoke = new Yoke(vertx);
        yoke.use((request, next) -> {
            assertEquals(new Locale("da"), request.locale());
            testComplete();
        });

        // second time send the authorization header
        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("Accept-Language", "da, en-gb;q=0.8, en;q=0.7");

        new YokeTester(yoke).request(HttpMethod.GET, "/", headers, null);
        await();
    }

    @Test
    public void testLocale3() {
        Yoke yoke = new Yoke(vertx);
        yoke.use((request, next) -> {
            assertEquals(new Locale("en", "gb"), request.locale());
            testComplete();
        });

        // second time send the authorization header
        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("Accept-Language", "en-gb");

        new YokeTester(yoke).request(HttpMethod.GET, "/", headers, null);
        await();
    }
}
