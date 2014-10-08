package com.jetdrone.vertx.yoke.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;

public class IEXSSTest extends VertxTestBase {

    public static final String IE_7 = "Mozilla/5.0 (Windows; U; MSIE 7.0; Windows NT 6.0; en-US)";
    public static final String IE_8 = "Mozilla/4.0 ( ; MSIE 8.0; Windows NT 6.0; Trident/4.0; GTB6.6; .NET CLR 3.5.30729)";
    public static final String IE_9 = "Mozilla/5.0 (Windows; U; MSIE 9.0; WIndows NT 9.0; en-US)";
    public static final String FIREFOX_23 = "Mozilla/5.0 (Windows NT 6.2; rv:22.0) Gecko/20130405 Firefox/23.0";

    @Test
    public void setsHeaderForFirefox23() {
        final Yoke app = new Yoke(vertx);
        app.use(new IEXSS());
        app.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                request.response().end("hello");
            }
        });

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("User-Agent", FIREFOX_23);

        new YokeTester(app).request("GET", "/", headers, response -> {
            assertEquals(response.headers().get("X-XSS-Protection"), "1; mode=block");
            testComplete();
        });
    }

    @Test
    public void setsHeaderForIE9() {
        final Yoke app = new Yoke(vertx);
        app.use(new IEXSS());
        app.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                request.response().end("hello");
            }
        });

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("User-Agent", IE_9);

        new YokeTester(app).request("GET", "/", headers, response -> {
            assertEquals(response.headers().get("X-XSS-Protection"), "1; mode=block");
            testComplete();
        });
    }

    @Test
    public void setsHeaderTo0ForIE8() {
        final Yoke app = new Yoke(vertx);
        app.use(new IEXSS());
        app.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                request.response().end("hello");
            }
        });

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("User-Agent", IE_8);

        new YokeTester(app).request("GET", "/", headers, response -> {
            assertEquals(response.headers().get("X-XSS-Protection"), "0");
            testComplete();
        });
    }

    @Test
    public void setsHeaderTo0ForIE7() {
        final Yoke app = new Yoke(vertx);
        app.use(new IEXSS());
        app.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                request.response().end("hello");
            }
        });

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("User-Agent", IE_7);

        new YokeTester(app).request("GET", "/", headers, response -> {
            assertEquals(response.headers().get("X-XSS-Protection"), "0");
            testComplete();
        });
    }

    @Test
    public void allowsYouToSetTheHeaderForOldIE() {
        final Yoke app = new Yoke(vertx);
        app.use(new IEXSS(true));
        app.use(new Handler<YokeRequest>() {
            @Override
            public void handle(YokeRequest request) {
                request.response().end("hello");
            }
        });

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("User-Agent", IE_8);

        new YokeTester(app).request("GET", "/", headers, response -> {
            assertEquals(response.headers().get("X-XSS-Protection"), "1; mode=block");
            testComplete();
        });
    }

}
