package com.jetdrone.vertx.yoke.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import io.vertx.core.Handler;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;

public class HandlebarsEngineTest extends VertxTestBase {
	
		private static final String NEWLINE = System.getProperty("line.separator");

    @Test
    public void testEngine() {
        try {
            Yoke yoke = new Yoke(vertx);
            yoke.engine("hbs", new HandlebarsEngine("target/test-classes/views"));
            yoke.use((request, next) -> {
                request.put("name", "Paulo");
                request.response().render("template.hbs", next);
            });

            new YokeTester(yoke).request(HttpMethod.GET, "/", resp -> {
                assertEquals(200, resp.getStatusCode());
                assertEquals("Hello Paulo!", resp.body.toString());
                testComplete();
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
        await();
    }

    @Test
    public void testEngine2() {
        try {
            Yoke yoke = new Yoke(vertx);
            yoke.engine("hbs", new HandlebarsEngine("target/test-classes/views"));
            yoke.use((request, next) -> {
                List<Map> blogs = new ArrayList<>();
                Map<String, String> blog1 = new HashMap<>();
                blog1.put("name", "Handlebars.java");
                blogs.add(blog1);

                Map<String, String> blog2 = new HashMap<>();
                blog2.put("name", "Handlebars.js");
                blogs.add(blog2);

                Map<String, String> blog3 = new HashMap<>();
                blog3.put("name", "Mustache");
                blogs.add(blog3);

                request.put("blogs", blogs);
                request.response().render("template2.hbs", next);
            });

            new YokeTester(yoke).request(HttpMethod.GET, "/", resp -> {
                assertEquals(200, resp.getStatusCode());
                assertEquals("<html><body><ul><li>Handlebars.java</li><li>Handlebars.js</li><li>Mustache</li></ul></body></html>", resp.body.toString());
                testComplete();
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
        await();
    }

    @Test
    public void testReuse() {
        Yoke yoke = new Yoke(vertx);
        yoke.engine("hbs", new HandlebarsEngine("target/test-classes"));
        yoke.use((request, next) -> request.response().render("views/home.hbs"));

        new YokeTester(yoke).request(HttpMethod.GET, "/", resp -> {
            assertEquals(200, resp.getStatusCode());
            assertEquals("<h1>Yoke</h1>" + NEWLINE +
                    "<p>Home page</p>" + NEWLINE +
                    "<span>Powered by Handlebars.java</span>", resp.body.toString());
            testComplete();
        });
        await();
    }

    @Test
    public void testPartials() {
        Yoke yoke = new Yoke(vertx);
        yoke.engine("hbs", new HandlebarsEngine("target/test-classes"));
        yoke.use((request, next) -> request.response().render("views/home2.hbs"));

        new YokeTester(yoke).request(HttpMethod.GET, "/", resp -> {
            assertEquals(200, resp.getStatusCode());
            assertEquals(NEWLINE +
                            NEWLINE +
                    "<h1>Yoke</h1>" + NEWLINE +
                    NEWLINE +
                    NEWLINE +
                    "<p>Home page</p>" + NEWLINE +
                    NEWLINE +
                    NEWLINE +
                    "<span>Powered by Handlebars.java</span>", resp.body.toString());
            testComplete();
        });
        await();
    }
}
