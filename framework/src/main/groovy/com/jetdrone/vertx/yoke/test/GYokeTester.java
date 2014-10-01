/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.test;

import com.jetdrone.vertx.yoke.GYoke;
import groovy.lang.Closure;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.CaseInsensitiveMultiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GYokeTester extends YokeTester {

    public GYokeTester(GYoke yoke, boolean fakeSSL) {
        super(yoke.toJavaYoke(), fakeSSL);
    }

    public GYokeTester(GYoke yoke) {
        this(yoke, false);
    }

    public void request(final String method, final String url, final Closure<Response> handler) {
        request(method, url, handler::call);
    }

    public void request(final String method, final String url, final Map<String, Object> headers, final Closure<Response> handler) {
        request(method, url, toMultiMap(headers), handler::call);
    }

    public void request(final String method, final String url, final Map<String, Object> headers, final Buffer body, final Closure<Response> handler) {
        request(method, url, toMultiMap(headers), body, handler::call);
    }

    private static MultiMap toMultiMap(Map<String, Object> headers) {
        if (headers == null) {
            return null;
        }

        MultiMap multiMap = new CaseInsensitiveMultiMap();

        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            Object o = entry.getValue();
            if (o != null) {
                if (o instanceof List) {
                    List<String> entries = new ArrayList<>();
                    for (Object v : (List) o) {
                        if (v != null) {
                            entries.add(v.toString());
                        }
                    }
                    multiMap.add(entry.getKey(), entries);
                    continue;
                }
                if (o instanceof String) {
                    multiMap.add(entry.getKey(), (String) o);
                }
            }
        }

        return multiMap;
    }
}
