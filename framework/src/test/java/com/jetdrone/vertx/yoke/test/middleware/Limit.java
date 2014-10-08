package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;

public class Limit extends VertxTestBase {

    @Test
    public void testLimit() {
        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Limit(1000));

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("content-type", "text/plain");
        headers.add("content-length", "1024");

        Buffer body = Buffer.buffer(1024);

        for (int i=0; i < 1024; i++) {
            body.appendByte((byte) 'A');
        }

        new YokeTester(yoke).request("GET", "/", headers, body, resp -> {
            assertEquals(413, resp.getStatusCode());
            testComplete();
        });
    }
}
