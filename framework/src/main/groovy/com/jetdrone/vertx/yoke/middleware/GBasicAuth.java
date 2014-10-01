/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.middleware;

import groovy.lang.Closure;

public class GBasicAuth extends BasicAuth {		
		public GBasicAuth(final Closure handler) {				
				super(handler::call);
    }
		
		public GBasicAuth(String realm, final Closure handler) {				
				super(realm, handler::call);
    }
}
