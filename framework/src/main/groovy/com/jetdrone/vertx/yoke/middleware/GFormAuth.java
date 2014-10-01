/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.middleware;

import groovy.lang.Closure;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

public class GFormAuth extends FormAuth {

    public GFormAuth(final Closure authHandler) {
        super(authHandler::call);
    }

    public GFormAuth(boolean forceSSL, final Closure authHandler) {
        super(forceSSL, authHandler::call);
    }

    public GFormAuth(boolean forceSSL, String loginURI, String logoutURI, String loginTemplate, final Closure authHandler) {
        super(forceSSL, loginURI, logoutURI, loginTemplate, authHandler::call);
    }
}
