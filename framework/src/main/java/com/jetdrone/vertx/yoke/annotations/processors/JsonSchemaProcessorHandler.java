package com.jetdrone.vertx.yoke.annotations.processors;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.annotations.*;
import com.jetdrone.vertx.yoke.annotations.JsonSchema;
import com.jetdrone.vertx.yoke.json.*;
import com.jetdrone.vertx.yoke.middleware.Router;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import org.jetbrains.annotations.NotNull;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JsonSchemaProcessorHandler extends AbstractAnnotationHandler<Router> {

    public JsonSchemaProcessorHandler() {
        super(Router.class);
    }

    @Override
    public void process(Router router, Object instance, Class<?> clazz, Method method) {
        JsonSchema jsonSchema = Processor.getAnnotation(method, JsonSchema.class);

        if (jsonSchema == null) {
            return;
        }

        if (Processor.isCompatible(method, POST.class, YokeRequest.class, Handler.class)) {
            router.post(Processor.getAnnotation(method, POST.class).value(), wrap(JsonSchemaResolver.resolveSchema(jsonSchema.value())));
        }
        if (Processor.isCompatible(method, PUT.class, YokeRequest.class, Handler.class)) {
            router.put(Processor.getAnnotation(method, PUT.class).value(), wrap(JsonSchemaResolver.resolveSchema(jsonSchema.value())));
        }
        if (Processor.isCompatible(method, PATCH.class, YokeRequest.class, Handler.class)) {
            router.patch(Processor.getAnnotation(method, PATCH.class).value(), wrap(JsonSchemaResolver.resolveSchema(jsonSchema.value())));
        }
    }

    private static Middleware wrap(final JsonSchemaResolver.Schema schema) {
        return (request, next) -> {
            if (!com.jetdrone.vertx.yoke.json.JsonSchema.conformsSchema(request.body(), schema)) {
                next.handle(400);
                return;
            }

            // the request can be handled, it does respect the content negotiation
            next.handle(null);
        };
    }

    @Override
    public void process(Router router, Object instance, Class<?> clazz, Field field) {
        // NO-OP
    }
}
