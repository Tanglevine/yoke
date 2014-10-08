package com.jetdrone.vertx.yoke.test.middleware;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.test.core.VertxTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

public class Compress extends VertxTestBase {
  @Test
  public void testGzipCompress() {
    Yoke yoke = new Yoke(vertx);
    yoke.use(new com.jetdrone.vertx.yoke.middleware.Compress());
    yoke.use(new Middleware() {
      @Override
      public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
        request.response().end(new JsonObject().putString("hello", "world"));
      }
    });

    MultiMap headers = new CaseInsensitiveHeaders();
    headers.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    headers.add("Accept-Encoding", "gzip,deflate,sdch");

      new YokeTester(yoke).request("GET", "/", headers, resp -> {
          assertEquals(200, resp.getStatusCode());
          for (int i = 0; i < resp.body.length(); i++) {
//                  System.out.println((int) resp.body.getByte(i));
          }
          testComplete();
      });
  }

//  @Test
//  public void testGZip() {
//    Yoke yoke = new Yoke(vertx);
//    yoke.use(new com.jetdrone.vertx.yoke.middleware.Compress());
//    yoke.use(new Middleware() {
//      @Override
//      public void handle(YokeRequest request, Handler<Object> next) {
//        request.response().setContentType("text/plain", "UTF-8");
//        request.response().setChunked(true);
//
//        request.response().write("hello0000000000000000000000000000000000000000000000000\n");
//        request.response().write("hello0000000000000000000000000000000000000000000000000\n");
//        request.response().write("hello0000000000000000000000000000000000000000000000000\n");
//        request.response().write("hello0000000000000000000000000000000000000000000000000\n");
//        request.response().write("hello0000000000000000000000000000000000000000000000000\n");
//        request.response().write("hello0000000000000000000000000000000000000000000000000\n");
//        request.response().write("hello0000000000000000000000000000000000000000000000000\n");
//        request.response().write("hello0000000000000000000000000000000000000000000000000\n");
//        request.response().write("hello0000000000000000000000000000000000000000000000000\n");
//        request.response().write("hello0000000000000000000000000000000000000000000000000\n");
//        request.response().write("hello0000000000000000000000000000000000000000000000000\n");
//        request.response().end("hello0000000000000000000000000000000000000000000000000\n");
//
//
//      }
//    });
//
//    yoke.listen(8888);
//  }
}
