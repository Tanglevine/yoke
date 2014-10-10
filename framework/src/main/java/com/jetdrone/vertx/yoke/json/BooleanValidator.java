package com.jetdrone.vertx.yoke.json;

public final class BooleanValidator {

    public static boolean isValid(Object instance, JsonSchemaResolver.Schema schema) {
        if(!isBoolean(instance)) {
            return false;
        }

        // apply default value
        if (instance == null) {
            instance = schema.get("default");
        }

        return instance instanceof Boolean;
    }

    private static boolean isBoolean(Object value) {
        return value == null || value instanceof Boolean;
    }
}
