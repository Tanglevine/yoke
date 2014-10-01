package com.jetdrone.vertx.yoke.middleware;

import groovy.lang.Closure;
import org.jetbrains.annotations.Nullable;

public class GJWT extends JWT {
    public GJWT() {
        super();
    }

    public GJWT(final @Nullable String skip) {
        super(skip);
    }

    public GJWT(final @Nullable String skip, final Closure closure) {
        super(skip, (token, result) -> closure.call(token.toMap(), result));
    }
}
