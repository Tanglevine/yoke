package com.jetdrone.vertx.yoke.engine;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.ErrorHandler;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.Response;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import io.vertx.core.Handler;

public class Issue88Test extends VertxTestBase {

    @Test
    public void testIssue88() {
        Yoke yoke = new Yoke(vertx)
                .set("title", "Yoke 1.0.7: Issue #88")
                .engine("hbs", new HandlebarsEngine("target/test-classes/issue88/"))
                .use(new ErrorHandler(true))
                .use("/$", new Middleware() {
                            @Override
                            public void handle(
                                    @NotNull YokeRequest request,
                                    @NotNull Handler<Object> next) {
                                try {
                                    request.response().render("index.hbs", next);
                                } catch (Exception e) {
                                    next.handle(e);
                                }
                            }
                        }
                )
                .listen(8080);


        new YokeTester(yoke).request(HttpMethod.GET, "/$", resp -> {
            assertEquals(200, resp.getStatusCode());
            System.out.println(resp.body.toString());
            testComplete();
        });
        await();
    }
}
