package com.jetdrone.vertx.yoke.test.middleware;

import org.junit.Test;

import java.util.regex.Pattern;

public class JMXNameTest {

    @Test
    public void issue121() {
        //route everything start with hello
        Pattern p = Pattern.compile("^/hello(/|$).*");
        new com.jetdrone.vertx.yoke.middleware.Router().get(p, (request, next) -> {

        });
    }
}
