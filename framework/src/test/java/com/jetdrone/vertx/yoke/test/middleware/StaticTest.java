package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Static;
import com.jetdrone.vertx.yoke.test.YokeTester;
import org.junit.Ignore;
import org.junit.Test;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.testComplete;

public class StaticTest extends TestVerticle {

    @Test
    public void testStaticSimple() {

        Yoke yoke = new Yoke(this);
        yoke.use(new Static("static"));

        new YokeTester(yoke).request("GET", "/dir1/file.1", resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
    }

    @Test
    public void testStaticSimpleNotFound() {

        Yoke yoke = new Yoke(this);
        yoke.use(new Static("static"));

        new YokeTester(yoke).request("GET", "/dir1/file.2", resp -> {
            assertEquals(404, resp.getStatusCode());
            testComplete();
        });
    }

    @Test
    @Ignore
    // TODO: wait for bugfix from Vert.x 2.1.2
    public void testStaticEscape() {
        Yoke yoke = new Yoke(this);
        yoke.use(new Static("static"));

        new YokeTester(yoke).request("GET", "/dir1/new%20file.1", resp -> {
            assertEquals(200, resp.getStatusCode());
            testComplete();
        });
    }
}
