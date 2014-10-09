/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.test;

import com.jetdrone.vertx.yoke.Yoke;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.streams.ReadStream;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * # YokeTester
 */
public class YokeTester {

    private static final Random random = new Random();

    private final Vertx vertx;
    private final HttpServer fakeServer = new FakeHttpServer();
    private final boolean ssl;

    public YokeTester(Yoke yoke, boolean fakeSSL) {
        this.vertx = yoke.vertx();
        this.ssl = fakeSSL;
        yoke.listen(fakeServer);
    }

    public YokeTester(Yoke yoke) {
        this(yoke, false);
    }

    public void request(final HttpMethod method, final String url, final Handler<Response> handler) {
        request(method, url, new CaseInsensitiveHeaders(), Buffer.buffer(0), handler);
    }

    public void request(final HttpMethod method, final String url, final MultiMap headers, final Handler<Response> handler) {
        request(method, url, headers, Buffer.buffer(0), handler);
    }

    public void request(final HttpMethod method, final String url, final MultiMap headers, final Buffer body, final Handler<Response> handler) {
        try {
            final URI uri = new URI(url);
            final boolean urlEncoded = "application/x-www-form-urlencoded".equalsIgnoreCase(headers.get("content-type"));

            final Response response = new Response(vertx, handler);

            // start yoke
            fakeServer.requestHandler().handle(new HttpServerRequest() {

                MultiMap params = null;
                MultiMap attributes = null;
                boolean multipart = false;
                final NetSocket netSocket = new NetSocket() {
                    @Override
                    public String writeHandlerID() {
                        throw new UnsupportedOperationException("This mock does not support netSocket::writeHandlerID");
                    }

                    @Override
                    public NetSocket write(Buffer data) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::write");
                    }

                    @Override
                    public NetSocket write(String str) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::write");
                    }

                    @Override
                    public NetSocket write(String str, String enc) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::write");
                    }

                    @Override
                    public NetSocket sendFile(String filename) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::sendFile");
                    }

                    @Override
                    public NetSocket sendFile(String filename, Handler<AsyncResult<Void>> resultHandler) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::sendFile");
                    }

                    @Override
                    public SocketAddress remoteAddress() {
                        return new SocketAddress() {
                            @Override
                            public String hostAddress() {
                                return "localhost";
                            }

                            @Override
                            public int hostPort() {
                                return random.nextInt(Short.MAX_VALUE);
                            }
                        };
                    }

                    @Override
                    public SocketAddress localAddress() {
                        return new SocketAddress() {
                            @Override
                            public String hostAddress() {
                                return "localhost";
                            }

                            @Override
                            public int hostPort() {
                                return random.nextInt(Short.MAX_VALUE);
                            }
                        };
                    }

                    @Override
                    public void close() {
                        throw new UnsupportedOperationException("This mock does not support netSocket::close");
                    }

                    @Override
                    public NetSocket closeHandler(Handler<Void> handler) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::closeHandler");
                    }

                    @Override
                    public NetSocket upgradeToSsl(Handler<Void> handler) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::upgradeToSsl");
                    }

                    @Override
                    public boolean isSsl() {
                        return ssl;
                    }

                    @Override
                    public NetSocket setWriteQueueMaxSize(int maxSize) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::setWriteQueueMaxSize");
                    }

                    @Override
                    public boolean writeQueueFull() {
                        return false;
                    }

                    @Override
                    public NetSocket drainHandler(Handler<Void> handler) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::drainHandler");
                    }

                    @Override
                    public NetSocket endHandler(Handler<Void> endHandler) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::endHandler");
                    }

                    @Override
                    public NetSocket handler(Handler<Buffer> handler) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::dataHandler");
                    }

                    @Override
                    public NetSocket pause() {
                        throw new UnsupportedOperationException("This mock does not support netSocket::pause");
                    }

                    @Override
                    public NetSocket resume() {
                        throw new UnsupportedOperationException("This mock does not support netSocket::resume");
                    }

                    @Override
                    public NetSocket exceptionHandler(Handler<Throwable> handler) {
                        throw new UnsupportedOperationException("This mock does not support netSocket::exceptionHandler");
                    }
                };

                @Override
                public HttpVersion version() {
                    return HttpVersion.HTTP_1_1;
                }

                @Override
                public HttpMethod method() {
                    return method;
                }

                @Override
                public String uri() {
                    return uri.getPath() + (uri.getQuery() != null ? "?" + uri.getQuery() : "") + (uri.getFragment() != null ? "#" + uri.getFragment() : "");
                }

                @Override
                public String path() {
                    return uri.getPath();
                }

                @Override
                public String query() {
                    return uri.getQuery();
                }

                @Override
                public HttpServerResponse response() {
                    return response;
                }

                @Override
                public MultiMap headers() {
                    return headers;
                }

                @Override
                public MultiMap params() {
                    if (params == null) {
                        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri());
                        Map<String, List<String>> prms = queryStringDecoder.parameters();
                        params = new CaseInsensitiveHeaders();

                        if (!prms.isEmpty()) {
                            for (Map.Entry<String, List<String>> entry : prms.entrySet()) {
                                params.add(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    return params;
                }

                @Override
                public SocketAddress remoteAddress() {
                    return new SocketAddress() {
                        @Override
                        public String hostAddress() {
                            return "127.0.0.1";
                        }

                        @Override
                        public int hostPort() {
                            return 80;
                        }
                    };
                }

                @Override
                public SocketAddress localAddress() {
                    return new SocketAddress() {
                        @Override
                        public String hostAddress() {
                            return "127.0.0.1";
                        }

                        @Override
                        public int hostPort() {
                            return 81;
                        }
                    };
                }

                @Override
                public X509Certificate[] peerCertificateChain() throws SSLPeerUnverifiedException {
                    return null;
                }

                @Override
                public String absoluteURI() {
                    return url;
                }

                @Override
                public HttpServerRequest bodyHandler(final Handler<Buffer> bodyHandler) {
                    if (bodyHandler != null) {
                        vertx.runOnContext(new Handler<Void>() {
                            @Override
                            public void handle(Void event) {
                                bodyHandler.handle(body);
                            }
                        });
                    }
                    return this;
                }

                @Override
                public HttpServerRequest handler(final Handler<Buffer> handler) {
                    if (handler != null) {
                        vertx.runOnContext(new Handler<Void>() {
                            @Override
                            public void handle(Void event) {
                                handler.handle(body);
                            }
                        });
                    }
                    return this;
                }

                @Override
                public HttpServerRequest pause() {
                    throw new UnsupportedOperationException("This mock does not support pause");
                }

                @Override
                public HttpServerRequest resume() {
                    throw new UnsupportedOperationException("This mock does not support resume");
                }

                @Override
                public HttpServerRequest endHandler(final Handler<Void> endHandler) {
                    if (endHandler != null) {
                        vertx.runOnContext(new Handler<Void>() {
                            @Override
                            public void handle(Void event) {
                                endHandler.handle(null);
                            }
                        });
                    }
                    return this;
                }

                @Override
                public NetSocket netSocket() {
                    return netSocket;
                }

                @Override
                public boolean isExpectMultipart() {
                    return multipart;
                }

                @Override
                public HttpServerRequest setExpectMultipart(boolean expect) {
                    this.multipart = expect;
                    return this;
                }

                @Override
                public HttpServerRequest uploadHandler(Handler<HttpServerFileUpload> uploadHandler) {
                    throw new UnsupportedOperationException("This mock does not support uploadHandler");
                }

                @Override
                public MultiMap formAttributes() {
                    if (attributes == null) {
                        attributes = new CaseInsensitiveHeaders();
                        if (urlEncoded) {
                            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(body.toString(), false);

                            Map<String, List<String>> prms = queryStringDecoder.parameters();

                            if (!prms.isEmpty()) {
                                for (Map.Entry<String, List<String>> entry : prms.entrySet()) {
                                    attributes.add(entry.getKey(), entry.getValue());
                                }
                            }
                        }
                    }
                    return attributes;
                }

                @Override
                public HttpServerRequest exceptionHandler(Handler<Throwable> handler) {
                    throw new UnsupportedOperationException("This mock does not support exceptionHandler");
                }
            });
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static class FakeHttpServer implements HttpServer {

        Handler<HttpServerRequest> requestHandler;

        @Override
        public ReadStream<HttpServerRequest> requestStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public HttpServer requestHandler(Handler<HttpServerRequest> requestHandler) {
            this.requestHandler = requestHandler;
            return this;
        }

        @Override
        public Handler<HttpServerRequest> requestHandler() {
            return requestHandler;
        }

        @Override
        public ReadStream<ServerWebSocket> websocketStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public HttpServer websocketHandler(Handler<ServerWebSocket> wsHandler) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Handler<ServerWebSocket> websocketHandler() {
            throw new UnsupportedOperationException();
        }

        @Override
        public HttpServer listen() {
            return this;
        }

        @Override
        public HttpServer listen(Handler<AsyncResult<HttpServer>> listenHandler) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close(Handler<AsyncResult<Void>> doneHandler) {
            throw new UnsupportedOperationException();
        }
    }
}
