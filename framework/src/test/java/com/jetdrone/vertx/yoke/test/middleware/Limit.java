package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.test.Response;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Test;
import org.vertx.java.core.http.CaseInsensitiveMultiMap;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class Limit extends TestVerticle {

    @Test
    public void testLimit() {
        Yoke yoke = new Yoke(this);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Limit(1000));

        MultiMap headers = new CaseInsensitiveMultiMap();
        headers.add("content-type", "text/plain");
        headers.add("content-length", "1024");

        Buffer body = new Buffer(1024);

        for (int i=0; i < 1024; i++) {
            body.appendByte((byte) 'A');
        }

        new YokeTester(yoke).request("GET", "/", headers, body, resp -> {
            assertEquals(413, resp.getStatusCode());
            testComplete();
        });
    }
}
