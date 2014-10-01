/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.middleware;

import groovy.lang.Closure;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

public class GBasicAuth extends BasicAuth {		
		public GBasicAuth(final Closure handler) {				
				super(handler::call);
    }
		
		public GBasicAuth(String realm, final Closure handler) {				
				super(realm, handler::call);
    }
}
