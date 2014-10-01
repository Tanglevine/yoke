package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Test;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.CaseInsensitiveMultiMap;
import org.vertx.testtools.TestVerticle;

import java.util.Locale;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.testComplete;

public class RequestFunctions extends TestVerticle {

    @Test
    public void testAccepts() {
        Yoke yoke = new Yoke(this);
        yoke.use((request, next) -> request.response().end(request.accepts("text")));

        // second time send the authorization header
        MultiMap headers = new CaseInsensitiveMultiMap();
        headers.add("accept", "text/plain; q=0.5, application/json, text/html; q=0.8, text/xml");
        // expected order is:
        // application/json
        // text/xml
        // text/html
        // text/plain

        new YokeTester(yoke).request("GET", "/", headers, resp -> {
            assertEquals(200, resp.getStatusCode());
            assertEquals(resp.body.toString(), "text/xml");
            testComplete();
        });
    }

    @Test
    public void testIp() {
        Yoke yoke = new Yoke(this);
        yoke.use((request, next) -> {
            assertEquals("123.456.123.456", request.ip());
            testComplete();
        });

        // second time send the authorization header
        MultiMap headers = new CaseInsensitiveMultiMap();
        headers.add("x-forward-for", "123.456.123.456, 111.111.11.11");

        new YokeTester(yoke).request("GET", "/", headers, null);
    }

    @Test
    public void testLocale() {
        Yoke yoke = new Yoke(this);
        yoke.use((request, next) -> {
            assertEquals(new Locale("da", "dk"), request.locale());
            testComplete();
        });

        // second time send the authorization header
        MultiMap headers = new CaseInsensitiveMultiMap();
        headers.add("Accept-Language", "en-gb;q=0.8, en;q=0.7, da_DK;q=0.9");

        new YokeTester(yoke).request("GET", "/", headers, null);
    }

    @Test
    public void testLocale2() {
        Yoke yoke = new Yoke(this);
        yoke.use((request, next) -> {
            assertEquals(new Locale("da"), request.locale());
            testComplete();
        });

        // second time send the authorization header
        MultiMap headers = new CaseInsensitiveMultiMap();
        headers.add("Accept-Language", "da, en-gb;q=0.8, en;q=0.7");

        new YokeTester(yoke).request("GET", "/", headers, null);
    }

    @Test
    public void testLocale3() {
        Yoke yoke = new Yoke(this);
        yoke.use((request, next) -> {
            assertEquals(new Locale("en", "gb"), request.locale());
            testComplete();
        });

        // second time send the authorization header
        MultiMap headers = new CaseInsensitiveMultiMap();
        headers.add("Accept-Language", "en-gb");

        new YokeTester(yoke).request("GET", "/", headers, null);
    }
}
