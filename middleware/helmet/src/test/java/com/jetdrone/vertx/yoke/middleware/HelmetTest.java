package com.jetdrone.vertx.yoke.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.test.Response;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;

public class HelmetTest extends VertxTestBase {

    @Test
    public void testCacheControl() {
        final Yoke app = new Yoke(vertx);
        app.use(new CacheControl());
        app.use(request -> request.response().end("hello"));

        new YokeTester(app).request(HttpMethod.GET, "/", response -> {
            assertEquals(response.headers().get("Cache-Control"), "no-store, no-cache");
            testComplete();
        });
    }

    @Test
    public void testContentTypeOptions() {
        final Yoke app = new Yoke(vertx);
        app.use(new ContentTypeOptions());
        app.use(request -> request.response().end("hello"));

        new YokeTester(app).request(HttpMethod.GET, "/", response -> {
            assertEquals(response.headers().get("X-Content-Type-Options"), "nosniff");
            testComplete();
        });
    }

    @Test
    public void testCrossDomain() {
        final Yoke app = new Yoke(vertx);
        app.use(new CrossDomain());
        app.use(request -> request.response().end("hello"));

        final YokeTester tester = new YokeTester(app);

        tester.request(HttpMethod.GET, "/", response -> {
            assertEquals(response.body.toString(), "hello");

            tester.request(HttpMethod.GET, "/crossdomain.xml", response1 -> {
                assertEquals(response1.headers().get("Content-Type"), "text/x-cross-domain-policy");
                assertEquals(response1.body.toString(), "<?xml version=\"1.0\"?>" +
                        "<!DOCTYPE cross-domain-policy SYSTEM \"http://www.adobe.com/xml/dtds/cross-domain-policy.dtd\">" +
                        "<cross-domain-policy>" +
                        "<site-control permitted-cross-domain-policies=\"none\"/>" +
                        "</cross-domain-policy>");

                testComplete();
            });
        });
    }

    @Test
    public void testIENoOpen() {
        final Yoke app = new Yoke(vertx);
        app.use(new IENoOpen());
        app.use(request -> {
            request.response().putHeader("Content-Disposition", "attachment; filename=somefile.txt");
            request.response().end("hello");
        });

        new YokeTester(app).request(HttpMethod.GET, "/", response -> {
            assertEquals(response.headers().get("X-Download-Options"), "noopen");
            testComplete();
        });
    }

    @Test
    public void testHSTS_1() {
        final Yoke app = new Yoke(vertx);
        app.use(new HSTS());
        app.use(request -> request.response().end("hello"));

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("x-forwarded-proto", "https");

        new YokeTester(app).request(HttpMethod.GET, "/", headers, response -> {
            assertEquals(response.headers().get("Strict-Transport-Security"), "max-age=15768000");
            testComplete();
        });

    }

    @Test
    public void testHSTS_2() {
        final Yoke app = new Yoke(vertx);
        app.use(new HSTS(1234, true));
        app.use(request -> request.response().end("hello"));

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("x-forwarded-proto", "https");

        new YokeTester(app).request(HttpMethod.GET, "/", headers, response -> {
            assertEquals(response.headers().get("Strict-Transport-Security"), "max-age=1234; includeSubdomains");
            testComplete();
        });

    }
}
