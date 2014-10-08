package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Static;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.test.core.VertxTestBase;
import org.junit.Ignore;
import org.junit.Test;

public class StaticTest extends VertxTestBase {

    @Test
    public void testStaticSimple() {

        Yoke yoke = new Yoke(vertx);
        yoke.use(new Static("target/test-classes/static"));

        new YokeTester(yoke).request("GET", "/dir1/file.1", resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
    }

    @Test
    public void testStaticSimpleNotFound() {

        Yoke yoke = new Yoke(vertx);
        yoke.use(new Static("target/test-classes/static"));

        new YokeTester(yoke).request("GET", "/dir1/file.2", resp -> {
            assertEquals(404, resp.getStatusCode());
            testComplete();
        });
    }

    @Test
    @Ignore
    // TODO: wait for bugfix from Vert.x 2.1.2
    public void testStaticEscape() {
        Yoke yoke = new Yoke(vertx);
        yoke.use(new Static("target/test-classes/static"));

        new YokeTester(yoke).request("GET", "/dir1/new%20file.1", resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
    }
}
