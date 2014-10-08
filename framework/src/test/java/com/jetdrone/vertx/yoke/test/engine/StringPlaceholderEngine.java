package com.jetdrone.vertx.yoke.test.engine;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.engine.Function;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.test.YokeTester;
import io.vertx.test.core.VertxTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import io.vertx.core.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class StringPlaceholderEngine extends VertxTestBase {

    @Test
    public void testEngine() {
        try {
            // create a temp template
            File temp = File.createTempFile("template", ".shtml");
            FileOutputStream out = new FileOutputStream(temp);
            out.write("Hello ${name}!".getBytes());
            out.close();
            final String location = temp.getAbsolutePath();

            Yoke yoke = new Yoke(vertx);
            yoke.engine("shtml", new com.jetdrone.vertx.yoke.engine.StringPlaceholderEngine(""));
            yoke.use(new Middleware() {
                @Override
                public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                    request.put("name", "Paulo");
                    request.response().render(location, next);
                }
            });

            new YokeTester(yoke).request("GET", "/", resp -> {
                assertEquals(200, resp.getStatusCode());
                assertEquals("Hello Paulo!", resp.body.toString());
                testComplete();
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testEngineFunctions() {
        try {
            // create a temp template
            File temp = File.createTempFile("template", ".shtml");
            FileOutputStream out = new FileOutputStream(temp);
            out.write("Hello ${fnName('Lopes')}!".getBytes());
            out.close();
            final String location = temp.getAbsolutePath();

            Yoke yoke = new Yoke(vertx);
            yoke.set("fnName", new Function() {
                @Override
                public String exec(Map<String, Object> context, Object... args) {
                    return "Paulo " + args[0];
                }
            });
            yoke.engine("shtml", new com.jetdrone.vertx.yoke.engine.StringPlaceholderEngine(""));
            yoke.use(new Middleware() {
                @Override
                public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                    request.put("name", "Paulo");
                    request.response().render(location, next);
                }
            });

            new YokeTester(yoke).request("GET", "/", resp -> {
                assertEquals(200, resp.getStatusCode());
                assertEquals("Hello Paulo Lopes!", resp.body.toString());
                testComplete();
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

//    @Test
//    public void testRG() {
//        String funcName = "([a-zA-Z0-9]+)";
//        String arguments = "\\((.*)\\)";
//        Pattern FUNCTION = Pattern.compile(funcName + "\\s*" + arguments);
//
////        Matcher f = FUNCTION.matcher("func()");
//        Matcher f = FUNCTION.matcher("func(\"arg\")");
////        Matcher f = FUNCTION.matcher("func(\"arg\", \"arg2\")");
//        if (f.find()) {
//            System.out.println("It is a function");
//
//            String argument = "(.*?)";
//            String quote = "\"";
//            String sep = "(,\\s*)?";
//            Pattern ARG = Pattern.compile(quote + argument + quote + sep);
//
//            Matcher a = ARG.matcher(f.group(2));
//
//            while (a.find()) {
//                System.out.println("It has argument: " + a.group(1));
//            }
//        }
//
//        testComplete();
//    }
}
