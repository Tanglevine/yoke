package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Vhost;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;

public class VHost extends VertxTestBase {

    @Test
    public void testLimit() {
        Yoke yoke = new Yoke(vertx);
        yoke.use(new Vhost("*.com", request -> {
            request.response().end();
            testComplete();
        }));
        yoke.use(request -> {
            request.response().end();
            fail();
        });

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("host", "www.mycorp.com");

        new YokeTester(yoke).request(HttpMethod.GET, "/", headers, null);
        await();
    }
}
