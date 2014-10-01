/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.middleware;

import groovy.lang.Closure;

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
