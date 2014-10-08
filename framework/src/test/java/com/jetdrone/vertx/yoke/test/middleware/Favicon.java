package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.util.Utils;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.buffer.Buffer;

public class Favicon extends VertxTestBase {

    @Test
    public void testFavicon() {
        Yoke yoke = new Yoke(vertx);
        yoke.use(new com.jetdrone.vertx.yoke.middleware.Favicon());

        final Buffer icon = Utils.readResourceToBuffer(com.jetdrone.vertx.yoke.middleware.Favicon.class, "favicon.ico");

        new YokeTester(yoke).request("GET", "/favicon.ico", resp -> {
            assertEquals(200, resp.getStatusCode());
            assertArrayEquals(icon.getBytes(), resp.body.getBytes());
            testComplete();
        });
    }
}
