package com.jetdrone.vertx.yoke.core.impl;

import com.jetdrone.vertx.yoke.Engine;
import com.jetdrone.vertx.yoke.core.Context;
import com.jetdrone.vertx.yoke.middleware.YokeResponse;
import io.netty.handler.codec.http.Cookie;
import org.mozilla.javascript.*;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.streams.ReadStream;

import java.util.Map;

import static com.jetdrone.vertx.yoke.core.impl.JSUtil.*;

final class JSYokeResponse  extends YokeResponse implements Scriptable {

    public JSYokeResponse(HttpServerResponse response, Context context, Map<String, Engine> engines) {
        super(response, context, engines);
    }

    private Scriptable prototype, parent;

    // cacheable scriptable/callable objects

    private Callable addCookie;
    private Callable close;
    private Callable closeHandler;
    private Callable drainHandler;
    private Callable end;
    private Callable endHandler;
    private Callable exceptionHandler;
    private Callable getHeader;
    private Object headers;
    private Callable headersHandler;
    private Callable jsonp;
    private Callable putHeader;
    private Callable putTrailer;
    private Callable redirect;
    private Callable render;
    private Callable sendFile;
    private Callable setContentType;
    private Callable setWriteQueueMaxSize;
    private Object trailers;
    private Callable write;

    @Override
    public String getClassName() {
        return "JSYokeResponse";
    }

    @Override
    public Object get(String name, Scriptable start) {
        switch (name) {
            case "addCookie":
                if (addCookie == null) {
                    addCookie = (cx, scope, thisObj, args) -> {
                        if (JSYokeResponse.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                        }

                        if (is(args, Cookie.class)) {
                            JSYokeResponse.this.addCookie((Cookie) args[0]);
                            return Undefined.instance;
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return addCookie;
            case "close":
                if (close == null) {
                    close = (cx, scope, thisObj, args) -> {
                        if (JSYokeResponse.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                        }
                        JSYokeResponse.this.close();
                        return Undefined.instance;
                    };
                }
                return close;
            case "closeHandler":
                if (closeHandler == null) {
                    closeHandler = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeResponse.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                            }

                            if (is(args, Handler.class)) {
                                JSYokeResponse.this.closeHandler((Handler) args[0]);
                                return Undefined.instance;
                            }

                            if (is(args, Callable.class)) {
                                JSYokeResponse.this.closeHandler(event -> ((Callable) args[0]).call(cx, scope, thisObj, EMPTY_OBJECT_ARRAY));
                                return Undefined.instance;
                            }

                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return closeHandler;
            case "drainHandler":
                if (drainHandler == null) {
                    drainHandler = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeResponse.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                            }

                            if (is(args, Handler.class)) {
                                JSYokeResponse.this.drainHandler((Handler) args[0]);
                                return Undefined.instance;
                            }

                            if (is(args, Callable.class)) {
                                JSYokeResponse.this.drainHandler(event -> ((Callable) args[0]).call(cx, scope, thisObj, EMPTY_OBJECT_ARRAY));
                                return Undefined.instance;
                            }

                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return drainHandler;
            case "end":
                if (end == null) {
                    end = (cx, scope, thisObj, args) -> {
                        if (JSYokeResponse.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                        }

                        if (is(args, NativeObject.class) || is(args, NativeArray.class)) {
                            Object json = NativeJSON.stringify(cx, scope, args[0], null, null);
                            if (json instanceof String) {
                                JSYokeResponse.this.end((String) json);
                                return Undefined.instance;
                            }
                            throw new UnsupportedOperationException();
                        }

                        if (is(args, String.class, String.class)) {
                            JSYokeResponse.this.end((String) args[0], (String) args[1]);
                            return Undefined.instance;
                        }

                        if (is(args, JsonElement.class)) {
                            JSYokeResponse.this.end((JsonElement) args[0]);
                            return Undefined.instance;
                        }

                        if (is(args, ReadStream.class)) {
                            JSYokeResponse.this.end((ReadStream) args[0]);
                            return Undefined.instance;
                        }

                        if (is(args, String.class)) {
                            JSYokeResponse.this.end((String) args[0]);
                            return Undefined.instance;
                        }

                        if (is(args, Buffer.class)) {
                            JSYokeResponse.this.end((Buffer) args[0]);
                            return Undefined.instance;
                        }

                        if (is(args)) {
                            JSYokeResponse.this.end();
                            return Undefined.instance;
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return end;
            case "endHandler":
                if (endHandler == null) {
                    endHandler = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeResponse.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                            }

                            if (is(args, Handler.class)) {
                                JSYokeResponse.this.endHandler((Handler) args[0]);
                                return Undefined.instance;
                            }

                            if (is(args, Callable.class)) {
                                JSYokeResponse.this.endHandler(event -> ((Callable) args[0]).call(cx, scope, thisObj, EMPTY_OBJECT_ARRAY));
                                return Undefined.instance;
                            }

                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return endHandler;
            case "exceptionHandler":
                if (exceptionHandler == null) {
                    exceptionHandler = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeResponse.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                            }

                            if (is(args, Handler.class)) {
                                JSYokeResponse.this.exceptionHandler((Handler) args[0]);
                                return Undefined.instance;
                            }

                            if (is(args, Callable.class)) {
                                JSYokeResponse.this.exceptionHandler(throwable -> ((Callable) args[0]).call(cx, scope, thisObj, new Object[] {
                                        new WrappedException(throwable)}));
                                return Undefined.instance;
                            }

                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return exceptionHandler;
            case "getHeader":
                if (getHeader == null) {
                    getHeader = (cx, scope, thisObj, args) -> {
                        if (JSYokeResponse.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                        }

                        if (is(args, String.class, String.class)) {
                            return JSYokeResponse.this.getHeader((String) args[0], (String) args[1]);
                        }

                        if (is(args, String.class)) {
                            return JSYokeResponse.this.getHeader((String) args[0]);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return getHeader;
            case "statusCode":
                return getStatusCode();
            case "statusMessage":
                return getStatusMessage();
            case "headers":
                if (headers == null) {
                    headers = javaToJS(headers(), getParentScope());
                }
                return headers;
            case "headersHandler":
                if (headersHandler == null) {
                    headersHandler = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeResponse.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                            }

                            if (is(args, Handler.class)) {
                                JSYokeResponse.this.headersHandler((Handler) args[0]);
                                return Undefined.instance;
                            }

                            if (is(args, Callable.class)) {
                                JSYokeResponse.this.headersHandler(event -> ((Callable) args[0]).call(cx, scope, thisObj, EMPTY_OBJECT_ARRAY));
                                return Undefined.instance;
                            }

                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return headersHandler;
            case "chunked":
                return isChunked();
            case "jsonp":
                if (jsonp == null) {
                    jsonp = (cx, scope, thisObj, args) -> {
                        if (JSYokeResponse.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                        }

                        if (is(args, String.class, NativeObject.class) || is(args, String.class, NativeArray.class)) {
                            Object json = NativeJSON.stringify(cx, scope, args[1], null, null);
                            if (json instanceof String) {
                                JSYokeResponse.this.jsonp((String) args[0], (String) json);
                                return Undefined.instance;
                            }
                            throw new UnsupportedOperationException();
                        }

                        if (is(args, NativeObject.class) || is(args, NativeArray.class)) {
                            Object json = NativeJSON.stringify(cx, scope, args[0], null, null);
                            if (json instanceof String) {
                                JSYokeResponse.this.jsonp((String) json);
                                return Undefined.instance;
                            }
                            throw new UnsupportedOperationException();
                        }

                        if (is(args, String.class, JsonElement.class)) {
                            JSYokeResponse.this.jsonp((String) args[0], (JsonElement) args[1]);
                            return Undefined.instance;
                        }
                        if (is(args, String.class, String.class)) {
                            JSYokeResponse.this.jsonp((String) args[0], (String) args[1]);
                            return Undefined.instance;
                        }
                        if (is(args, JsonElement.class)) {
                            JSYokeResponse.this.jsonp((JsonElement) args[0]);
                            return Undefined.instance;
                        }
                        if (is(args, String.class)) {
                            JSYokeResponse.this.jsonp((String) args[0]);
                            return Undefined.instance;
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return jsonp;
            case "putHeader":
                if (putHeader == null) {
                    putHeader = (cx, scope, thisObj, args) -> {
                        if (JSYokeResponse.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                        }

                        if (is(args, CharSequence.class, CharSequence.class)) {
                            JSYokeResponse.this.putHeader((CharSequence) args[0], (CharSequence) args[1]);
                            return Undefined.instance;
                        }

                        if (is(args, CharSequence.class, Iterable.class)) {
                            JSYokeResponse.this.putHeader((CharSequence) args[0], (Iterable<CharSequence>) args[1]);
                            return Undefined.instance;
                        }

                        if (is(args, String.class, String.class)) {
                            JSYokeResponse.this.putHeader((String) args[0], (String) args[1]);
                            return Undefined.instance;
                        }

                        if (is(args, String.class, Iterable.class)) {
                            JSYokeResponse.this.putHeader((String) args[0], (Iterable<String>) args[1]);
                            return Undefined.instance;
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return putHeader;
            case "putTrailer":
                if (putTrailer == null) {
                    putTrailer = (cx, scope, thisObj, args) -> {
                        if (JSYokeResponse.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                        }

                        if (is(args, CharSequence.class, CharSequence.class)) {
                            JSYokeResponse.this.putTrailer((CharSequence) args[0], (CharSequence) args[1]);
                            return Undefined.instance;
                        }

                        if (is(args, CharSequence.class, Iterable.class)) {
                            JSYokeResponse.this.putTrailer((CharSequence) args[0], (Iterable<CharSequence>) args[1]);
                            return Undefined.instance;
                        }

                        if (is(args, String.class, String.class)) {
                            JSYokeResponse.this.putTrailer((String) args[0], (String) args[1]);
                            return Undefined.instance;
                        }

                        if (is(args, String.class, Iterable.class)) {
                            JSYokeResponse.this.putTrailer((String) args[0], (Iterable<String>) args[1]);
                            return Undefined.instance;
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return putTrailer;
            case "redirect":
                if (redirect == null) {
                    redirect = (cx, scope, thisObj, args) -> {
                        if (JSYokeResponse.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                        }

                        if (is(args, Integer.class, String.class)) {
                            JSYokeResponse.this.redirect((Integer) args[0], (String) args[1]);
                            return Undefined.instance;
                        }

                        if (is(args, String.class)) {
                            JSYokeResponse.this.redirect((String) args[0]);
                            return Undefined.instance;
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return redirect;
            case "render":
                if (render == null) {
                    render = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeResponse.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                            }

                            if (is(args, String.class, Handler.class)) {
                            	JSYokeResponse.this.render((String) args[0], (Handler) args[1]);
                                return Undefined.instance;
                            }
                            
                            if (is(args, String.class, Callable.class)) {
                            	
                                JSYokeResponse.this.render((String) args[0], error -> ((Callable) args[1]).call(cx, scope, thisObj, new Object[]{error}));
                                return Undefined.instance;
                            }
                            
                            if (is(args, String.class)) {
                                JSYokeResponse.this.render((String) args[0]);
                                return Undefined.instance;
                            }
                            
                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return render;
            case "sendFile":
                if (sendFile == null) {
                    sendFile = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeResponse.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                            }

                            if (is(args, String.class, String.class, Handler.class)) {
                                JSYokeResponse.this.sendFile((String) args[0], (String) args[1], (Handler) args[2]);
                                return Undefined.instance;
                            }

                            if (is(args, String.class, String.class, Callable.class)) {
                                JSYokeResponse.this.sendFile((String) args[0], (String) args[1], result -> ((Callable) args[2]).call(cx, scope, thisObj, new Object[]{result.cause(), result.result()}));
                                return Undefined.instance;
                            }

                            if (is(args, String.class, String.class)) {
                                JSYokeResponse.this.sendFile((String) args[0], (String) args[1]);
                                return Undefined.instance;
                            }

                            if (is(args, String.class, Handler.class)) {
                                JSYokeResponse.this.sendFile((String) args[0], (Handler) args[1]);
                                return Undefined.instance;
                            }

                            if (is(args, String.class, Callable.class)) {
                                JSYokeResponse.this.sendFile((String) args[0], result -> ((Callable) args[1]).call(cx, scope, thisObj, new Object[]{result.cause(), result.result()}));
                                return Undefined.instance;
                            }

                            if (is(args, String.class)) {
                                JSYokeResponse.this.sendFile((String) args[0]);
                                return Undefined.instance;
                            }

                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return sendFile;
            case "setContentType":
                if (setContentType == null) {
                    setContentType = (cx, scope, thisObj, args) -> {
                        if (JSYokeResponse.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                        }

                        if (is(args, String.class, String.class)) {
                            JSYokeResponse.this.setContentType((String) args[0], (String) args[1]);
                            return Undefined.instance;
                        }

                        if (is(args, String.class)) {
                            JSYokeResponse.this.setContentType((String) args[0]);
                            return Undefined.instance;
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return setContentType;
            case "setWriteQueueMaxSize":
                if (setWriteQueueMaxSize == null) {
                    setWriteQueueMaxSize = (cx, scope, thisObj, args) -> {
                        if (JSYokeResponse.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                        }

                        if (is(args, Integer.class)) {
                            JSYokeResponse.this.setWriteQueueMaxSize((Integer) args[0]);
                            return Undefined.instance;
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return setWriteQueueMaxSize;
            case "trailers":
                if (trailers == null) {
                    trailers = javaToJS(trailers(), getParentScope());
                }
                return trailers;
            case "write":
                if (write == null) {
                    write = (cx, scope, thisObj, args) -> {
                        if (JSYokeResponse.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeResponse]");
                        }

                        if (is(args, String.class, String.class)) {
                            JSYokeResponse.this.write((String) args[0], (String) args[1]);
                            return Undefined.instance;
                        }

                        if (is(args, String.class)) {
                            JSYokeResponse.this.write((String) args[0]);
                            return Undefined.instance;
                        }

                        if (is(args, Buffer.class)) {
                            JSYokeResponse.this.write((Buffer) args[0]);
                            return Undefined.instance;
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return write;
            case "writeQueueFull":
                return writeQueueFull();
            default:
                // fail to find
                return NOT_FOUND;
        }
    }

    @Override
    public Object get(int index, Scriptable start) {
        return NOT_FOUND;
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return false;
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return false;
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        switch (name) {
            case "chunked":
                setChunked((Boolean) value);
                return;
            case "statusCode":
                setStatusCode((Integer) value);
                return;
            case "statusMessage":
                setStatusMessage((String) value);
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        put(Integer.toString(index), start, value);
    }

    @Override
    public void delete(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int index) {
        delete(Integer.toString(index));
    }

    @Override
    public Scriptable getPrototype() {
        return prototype;
    }

    @Override
    public void setPrototype(Scriptable prototype) {
        this.prototype = prototype;
    }

    @Override
    public Scriptable getParentScope() {
        return parent;
    }

    @Override
    public void setParentScope(Scriptable parent) {
        this.parent = parent;
    }

    @Override
    public Object[] getIds() {
        return EMPTY_OBJECT_ARRAY;
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        return "[object JSYokeResponse]";
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        return instance != null && instance instanceof JSYokeResponse;
    }
}
