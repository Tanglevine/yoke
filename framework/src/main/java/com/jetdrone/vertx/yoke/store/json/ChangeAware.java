package com.jetdrone.vertx.yoke.store.json;

@FunctionalInterface
public interface ChangeAware {

	public void notifyChanged(ChangeAwareJsonElement jsonElement);

}
