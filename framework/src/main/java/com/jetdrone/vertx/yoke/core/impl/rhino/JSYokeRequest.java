package com.jetdrone.vertx.yoke.core.impl.rhino;

import static com.jetdrone.vertx.yoke.core.impl.rhino.JSUtil.EMPTY_OBJECT_ARRAY;
import static com.jetdrone.vertx.yoke.core.impl.rhino.JSUtil.isVararg;
import static com.jetdrone.vertx.yoke.core.impl.rhino.JSUtil.javaToJS;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrappedException;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;

import com.jetdrone.vertx.yoke.core.Context;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.store.SessionStore;

final class JSYokeRequest  extends YokeRequest implements Scriptable {

    private final Context context;

    private Callable resolveScope;
    private Callable accepts;
    private Callable bodyHandler;
    private Object cookies;
    private Callable handler;
    private Callable endHandler;
    private Callable exceptionHandler;
    private Callable setExpectMultipart;
    private Callable isExpectMultipart;
    private Object files;
    private Object formAttributes;

    private Scriptable prototype, parent;
    private Callable getAllCookies;
    private Callable getAllHeaders;
    private Callable getFile;
    private Callable getCookie;
    private Callable getFormParameter;
    private Callable getFormParameterList;
    private Callable getHeader;
    private Callable getParameter;
    private Callable getParameterList;
    private Object params;
    private Callable param;
    private Object headers;
    private Callable is;
    private Callable hasBody;
    private Callable loadSession;
    private Callable pause;
    private Callable resume;
    private Callable sortedHeader;
    private Callable uploadHandler;
    private Callable createSession;
    private Callable destroySession;

    public JSYokeRequest(HttpServerRequest request, JSYokeResponse response, Context context, SessionStore store) {
        super(request, response, context, store);
        this.context = context;
    }

    @Override
    public String getClassName() {
        return "JSYokeRequest";
    }

    @Override
    public Object get(String name, Scriptable start) {
        // first context
        if (context.containsKey(name)) {
            return javaToJS(context.get(name), getParentScope());
        }
        // cacheable scriptable objects
        switch (name) {
        	case "resolveScope":
        		if (resolveScope == null) {
        			resolveScope = (cx, scope, thisObj, args) -> {
                        setParentScope(scope);
                        ((JSYokeResponse) response()).setParentScope(scope);
                        return Undefined.instance;
                    };
        		}
        		return resolveScope;
            case "absoluteURI":
                return absoluteURI();
            case "accepts":
                if (accepts == null) {
                    accepts = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (isVararg(args, String.class)) {
                            return JSYokeRequest.this.accepts((String[]) args);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return accepts;
            case "body":
                if (body != null) {
                    if (body instanceof Buffer) {
                        return body.toString();
                    }
                    return javaToJS(body, getParentScope());
                }
                return null;
            case "bodyHandler":
                if (bodyHandler == null) {
                    bodyHandler = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeRequest.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                            }

                            if (JSUtil.is(args, Handler.class)) {
                                JSYokeRequest.this.bodyHandler((Handler) args[0]);
                                return Undefined.instance;
                            }

                            if (JSUtil.is(args, Callable.class)) {
                                JSYokeRequest.this.bodyHandler(buffer -> ((Callable) args[0]).call(cx, scope, thisObj, new Object[] {
                                        buffer.toString()
                                }));
                                return Undefined.instance;
                            }

                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return bodyHandler;
            case "bodyLengthLimit":
                return bodyLengthLimit();
            case "contentLength":
                return contentLength();
            case "cookies":
                if (cookies == null) {
                	cookies = javaToJS(cookies(), parent);
                }
                return cookies;
            case "createSession":
            	if (createSession == null) {
            		createSession = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }
                        return javaToJS(createSession(), scope);
                    };
            	}
                return createSession;
            case "handler":
                if (handler == null) {
                    handler = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeRequest.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                            }

                            if (JSUtil.is(args, Handler.class)) {
                                JSYokeRequest.this.handler((Handler) args[0]);
                                return Undefined.instance;
                            }

                            if (JSUtil.is(args, Callable.class)) {
                                JSYokeRequest.this.handler(buffer -> ((Callable) args[0]).call(cx, scope, thisObj, new Object[]{
                                        buffer.toString()
                                }));
                                return Undefined.instance;
                            }

                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return handler;
            case "destroySession":
            	if (destroySession == null) {
            		destroySession = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }
                        destroySession();
                        return Undefined.instance;
                    };
            	}
                return destroySession;
            case "endHandler":
                if (endHandler == null) {
                    endHandler = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeRequest.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                            }

                            if (JSUtil.is(args, Handler.class)) {
                                JSYokeRequest.this.endHandler((Handler) args[0]);
                                return Undefined.instance;
                            }

                            if (JSUtil.is(args, Callable.class)) {
                                JSYokeRequest.this.endHandler(v -> ((Callable) args[0]).call(cx, scope, thisObj, EMPTY_OBJECT_ARRAY));
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
                            if (JSYokeRequest.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                            }

                            if (JSUtil.is(args, Handler.class)) {
                                JSYokeRequest.this.exceptionHandler((Handler) args[0]);
                                return Undefined.instance;
                            }

                            if (JSUtil.is(args, Callable.class)) {
                                JSYokeRequest.this.exceptionHandler(throwable -> ((Callable) args[0]).call(cx, scope, thisObj, new Object[]{
                                        new WrappedException(throwable)}));
                                return Undefined.instance;
                            }

                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return exceptionHandler;
            case "isExpectMultipart":
                if (isExpectMultipart == null) {
                    isExpectMultipart = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        JSYokeRequest.this.isExpectMultipart();
                        return Undefined.instance;
                    };
                }
                return isExpectMultipart;
            case "setExpectMultipart":
                if (setExpectMultipart == null) {
                    setExpectMultipart = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, Boolean.class)) {
                            JSYokeRequest.this.setExpectMultipart((Boolean) args[0]);
                            return Undefined.instance;
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return setExpectMultipart;
            case "files":
                if (files == null) {
                	files = javaToJS(files(), getParentScope());
                }
                return files;
            case "formAttributes":
                if (formAttributes == null) {
                    formAttributes = javaToJS(formAttributes(), getParentScope());
                }
                return formAttributes;
            case "getAllCookies":
                if (getAllCookies == null) {
                    getAllCookies = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class)) {
                            return javaToJS(JSYokeRequest.this.getAllCookies((String) args[0]), scope);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return getAllCookies;
            case "getAllHeaders":
                if (getAllHeaders == null) {
                    getAllHeaders = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class)) {
                            return javaToJS(JSYokeRequest.this.getAllHeaders((String) args[0]), scope);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return getAllHeaders;
            case "getCookie":
                if (getCookie == null) {
                    getCookie = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class)) {
                            return javaToJS(JSYokeRequest.this.getCookie((String) args[0]), scope);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return getCookie;
            case "getFile":
                if (getFile == null) {
                    getFile = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class)) {
                            return javaToJS(JSYokeRequest.this.getFile((String) args[0]), scope);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return getFile;
            case "getFormParameter":
                if (getFormParameter == null) {
                    getFormParameter = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class, String.class)) {
                            return JSYokeRequest.this.getFormParameter((String) args[0], (String) args[1]);
                        }

                        if (JSUtil.is(args, String.class)) {
                            return JSYokeRequest.this.getFormParameter((String) args[0]);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return getFormParameter;
            case "getFormParameterList":
                if (getFormParameterList == null) {
                    getFormParameterList = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class)) {
                            return javaToJS(JSYokeRequest.this.getFormParameterList((String) args[0]), scope);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return getFormParameterList;
            case "getHeader":
                if (getHeader == null) {
                    getHeader = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class, String.class)) {
                            return JSYokeRequest.this.getHeader((String) args[0], (String) args[1]);
                        }

                        if (JSUtil.is(args, String.class)) {
                            return JSYokeRequest.this.getHeader((String) args[0]);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return getHeader;
            case "getParameter":
                if (getParameter == null) {
                    getParameter = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class, String.class)) {
                            return JSYokeRequest.this.getParameter((String) args[0], (String) args[1]);
                        }

                        if (JSUtil.is(args, String.class)) {
                            return JSYokeRequest.this.getParameter((String) args[0]);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return getParameter;
            case "getParameterList":
                if (getParameterList == null) {
                    getParameterList = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class)) {
                            return javaToJS(JSYokeRequest.this.getParameterList((String) args[0]), scope);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return getFormParameterList;
            case "params":
                if (params == null) {
                    params = javaToJS(params(), getParentScope());
                }
                return params;
            case "param":
            	if (param == null) {
            		param = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class)) {
                            String name1 = (String) args[0], value = null;
                            // first try to get param from params / url --> this differs from expressjs
                            value = JSYokeRequest.this.params().get(name1);
                            if (value == null) {
                                // then try to get param from body
                                try {
                                    value = JSYokeRequest.this.formAttributes().get(name1);
                                } catch (Exception ignore) { /* maybe throw IllegalStateExcpetion */ }
                            }
                            return value != null ? value : Undefined.instance;
                        }

                        throw new UnsupportedOperationException();
                    };
            	}
            	return param;
            case "hasBody":
                if (hasBody == null) {
                    hasBody = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }
                        return JSYokeRequest.this.hasBody();
                    };
                }
                return hasBody;
            case "headers":
                if (headers == null) {
                    headers = javaToJS(headers(), getParentScope());
                }
                return headers;
            case "ip":
                return ip();
            case "is":
                if (is == null) {
                    is = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class)) {
                            return JSYokeRequest.this.is((String) args[0]);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return is;
            case "secure":
                return isSecure();
            case "loadSession":
                if (loadSession == null) {
                    loadSession = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeRequest.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                            }

                            if (JSUtil.is(args, String.class, Handler.class)) {
                                JSYokeRequest.this.loadSession((String) args[0], (Handler) args[1]);
                                return Undefined.instance;
                            }

                            if (JSUtil.is(args, String.class, Callable.class)) {
                                JSYokeRequest.this.loadSession((String) args[0], error -> ((Callable) args[0]).call(cx, scope, thisObj, new Object[]{error}));
                                return Undefined.instance;
                            }

                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return loadSession;
            case "localAddress":
                return localAddress().toString();
            case "locale":
                return locale().toString();
            case "method":
                return method();
            case "netSocket":
            	// TODO
                return netSocket();
            case "normalizedPath":
                return normalizedPath();
            case "originalMethod":
                return originalMethod();
            case "path":
                return path();
            case "pause":
                if (pause == null) {
                    pause = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        JSYokeRequest.this.pause();
                        return Undefined.instance;
                    };
                }
                return pause;
            case "peerCertificateChain":
            	// TODO
                try {
                    return peerCertificateChain();
                } catch (SSLPeerUnverifiedException e) {
                    throw new RuntimeException(e);
                }
            case "query":
                return query();
            case "remoteAddress":
                return remoteAddress().toString();
            case "res":
            case "response":
                return response();
            case "resume":
                if (resume == null) {
                    resume = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        JSYokeRequest.this.resume();
                        return Undefined.instance;
                    };
                }
                return resume;
            case "sortedHeader":
                if (sortedHeader == null) {
                    sortedHeader = (cx, scope, thisObj, args) -> {
                        if (JSYokeRequest.this != thisObj) {
                            throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                        }

                        if (JSUtil.is(args, String.class)) {
                            return javaToJS(JSYokeRequest.this.sortedHeader((String) args[0]), scope);
                        }

                        throw new UnsupportedOperationException();
                    };
                }
                return sortedHeader;
            case "uploadHandler":
                if (uploadHandler == null) {
                    uploadHandler = new Callable() {
                        @Override
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        public Object call(final org.mozilla.javascript.Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
                            if (JSYokeRequest.this != thisObj) {
                                throw new RuntimeException("[native JSYokeFunction not bind to JSYokeRequest]");
                            }

                            if (JSUtil.is(args, Handler.class)) {
                                JSYokeRequest.this.uploadHandler((Handler) args[0]);
                                return Undefined.instance;
                            }

                            if (JSUtil.is(args, Callable.class)) {
                                JSYokeRequest.this.uploadHandler(httpServerFileUpload -> ((Callable) args[0]).call(cx, scope, thisObj, new Object[]{
                                        httpServerFileUpload
                                }));
                                return Undefined.instance;
                            }

                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return uploadHandler;
            case "uri":
                return uri();
            case "version":
                return version();
            case "vertxHttpServerRequest":
            	// TODO
                return vertxHttpServerRequest();

            default:
                // fail to find
                return NOT_FOUND;
        }
    }

    @Override
    public Object get(int index, Scriptable start) {
        return get(Integer.toString(index), start);
    }

    @Override
    public boolean has(String name, Scriptable start) {
        // first context
        if (context.containsKey(name)) {
            return true;
        }
        // fail to find
        return false;
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return has(Integer.toString(index), start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        context.put(name, value);
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        put(Integer.toString(index), start, value);
    }

    @Override
    public void delete(String name) {
        context.remove(name);
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
        return context.keySet().toArray();
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        return "[object JSYokeRequest]";
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        return instance != null && instance instanceof JSYokeRequest;
    }
}