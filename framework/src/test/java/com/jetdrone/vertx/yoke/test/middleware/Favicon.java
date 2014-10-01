package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.util.Utils;
import com.jetdrone.vertx.yoke.test.Response;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class Favicon extends TestVerticle {

    @Test
    public void testFavicon() {
        Yoke yoke = new Yoke(this);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Favicon());

        final Buffer icon = Utils.readResourceToBuffer(com.jetdrone.vertx.yoke.middleware.Favicon.class, "favicon.ico");

        new YokeTester(yoke).request("GET", "/favicon.ico", resp -> {
            assertEquals(200, resp.getStatusCode());
            assertArrayEquals(icon.getBytes(), resp.body.getBytes());
            testComplete();
        });
    }
}
