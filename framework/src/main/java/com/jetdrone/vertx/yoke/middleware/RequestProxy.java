/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.middleware;

import com.jetdrone.vertx.yoke.AbstractMiddleware;
import org.jetbrains.annotations.NotNull;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;

/** # RequestProxy
 *
 * RequestProxy provides web client a simple way to interact with other REST service
 * providers via Yoke, meanwhile Yoke could pre-handle authentication, logging and etc.
 *
 * In order to handler the proxy request properly, Bodyparser should be disabled for the
 * path matched by RequestProxy.
 */
public class RequestProxy extends AbstractMiddleware {

    private final String prefix;
    private final String host;
    private final int port;
    private final boolean secure;

    public RequestProxy(@NotNull final String prefix, @NotNull final String host, final int port, final boolean secure) {
        this.prefix = prefix;
        this.host = host;
        this.port = port;
        this.secure = secure;
    }

    public RequestProxy(@NotNull final String prefix, final int port, final boolean secure) {
        this(prefix, "localhost", port, secure);
    }

    @Override
    public void handle(@NotNull final YokeRequest req, @NotNull final Handler<Object> next) {
        if (!req.uri().startsWith(prefix)) {
          next.handle(null);
          return;
        }
        final String newUri = req.uri().replaceFirst(prefix, "");
        final HttpClient client = vertx().createHttpClient().setHost(host).setPort(port);

        if (secure) {
            client.setSSL(true);
        }
        
        final HttpClientRequest cReq = client.request(req.method(), newUri, cRes -> {
          req.response().setStatusCode(cRes.statusCode());
          req.response().headers().set(cRes.headers());
          req.response().setChunked(true);
          cRes.dataHandler(data -> req.response().write(data));
          cRes.endHandler(v -> req.response().end());
          cRes.exceptionHandler(next::handle);
        });
        cReq.headers().set(req.headers());
        cReq.setChunked(true);
        req.dataHandler(cReq::write);
        req.endHandler(v -> cReq.end());
    }
}
