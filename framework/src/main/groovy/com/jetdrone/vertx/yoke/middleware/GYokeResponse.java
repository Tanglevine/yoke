/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.middleware;

import com.jetdrone.vertx.yoke.Engine;
import com.jetdrone.vertx.yoke.core.Context;
import com.jetdrone.vertx.yoke.core.GMultiMap;
import com.jetdrone.vertx.yoke.core.JSON;
import groovy.lang.Closure;
import org.jetbrains.annotations.NotNull;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.groovy.core.buffer.Buffer;

import java.util.List;
import java.util.Map;

public class GYokeResponse extends YokeResponse implements org.vertx.groovy.core.http.HttpServerResponse {

    private GMultiMap headers;
    private GMultiMap trailers;

    public GYokeResponse(HttpServerResponse response, Context context, Map<String, Engine> engines) {
        super(response, context, engines);
    }

    public void closeHandler(final Closure closure) {
        this.closeHandler(v -> closure.call());
    }

    public GYokeResponse write(@NotNull Buffer buffer) {
        write(buffer.toJavaBuffer());
        return this;
    }

    public GYokeResponse write(@NotNull String chunk) {
        super.write(chunk);
        return this;
    }

    public GYokeResponse write(@NotNull String chunk, @NotNull String enc) {
        super.write(chunk, enc);
        return this;
    }

    @Override
    public GYokeResponse setStatusCode(int statusCode) {
        super.setStatusCode(statusCode);
        return this;
    }

    @Override
    public GYokeResponse putTrailer(String name, String value) {
        super.putTrailer(name, value);
        return this;
    }

    @Override
    public GYokeResponse sendFile(String filename) {
        super.sendFile(filename);
        return this;
    }

    @Override
    public GYokeResponse setWriteQueueMaxSize(int maxSize) {
        super.setWriteQueueMaxSize(maxSize);
        return this;
    }

    @Override
    public GYokeResponse setChunked(boolean chunked) {
        super.setChunked(chunked);
        return this;
    }

    @Override
    public GYokeResponse setStatusMessage(String statusMessage) {
        super.setStatusMessage(statusMessage);
        return this;
    }

    @Override
    public GYokeResponse putHeader(String name, Iterable<String> values) {
        super.putHeader(name, values);
        return this;
    }

    @Override
    public GYokeResponse putTrailer(String name, Iterable<String> values) {
        super.putTrailer(name, values);
        return this;
    }

    @Override
    public GYokeResponse sendFile(String filename, String notFoundFile) {
        super.sendFile(filename, notFoundFile);
        return this;
    }

    public GYokeResponse drainHandler(final Closure closure) {
        this.drainHandler(v -> closure.call());
        return this;
    }

    public void end(Buffer buffer) {
        end(buffer.toJavaBuffer());
    }

    public GYokeResponse leftShift(Buffer buffer) {
        return write(buffer);
    }

    public GYokeResponse leftShift(String s) {
        write(s);
        return this;
    }

    public GYokeResponse exceptionHandler(final Closure closure) {
        this.exceptionHandler(exception -> closure.call());

        return this;
    }

    public GMultiMap getHeaders() {
        if (headers == null) {
            headers = new GMultiMap(headers());
        }
        return headers;
    }

    public GMultiMap getTrailers() {
        if (trailers == null) {
            trailers = new GMultiMap(trailers());
        }
        return trailers;
    }

    public boolean isWriteQueueFull() {
        return writeQueueFull();
    }

    public void end(Map<String, Object> json) {
        setContentType("application/json", "UTF-8");
        end(JSON.encode(json));
    }

    public void end(List<Object> json) {
        setContentType("application/json", "UTF-8");
        end(JSON.encode(json));
    }

    public void jsonp(Map<String, Object> json) {
        jsonp("callback", json);
    }

    public void jsonp(List<Object> json) {
        jsonp("callback", json);
    }

    public void jsonp(String callback, Map<String, Object> json) {

        if (callback == null) {
            // treat as normal json response
            end(json);
            return;
        }

        String body = null;

        if (json != null) {
            body = JSON.encode(json);
        }

        jsonp(callback, body);
    }

    public void jsonp(String callback, List<Object> json) {

        if (callback == null) {
            // treat as normal json response
            end(json);
            return;
        }

        String body = null;

        if (json != null) {
            body = JSON.encode(json);
        }

        jsonp(callback, body);
    }

    public GYokeResponse sendFile(String filename, final Closure resultHandler) {
        sendFile(filename, resultHandler::call);
        return this;
    }

    public GYokeResponse sendFile(String filename, String notFoundFile, final Closure resultHandler) {
        sendFile(filename, notFoundFile, resultHandler::call);
        return this;
    }

    public void render(final String template, final Closure<Object> next) {
        render(template, next::call);
    }
    
    @Override
    public GYokeResponse putHeader(String name, String value) {
        super.putHeader(name, value);
        return this;
    }
}
