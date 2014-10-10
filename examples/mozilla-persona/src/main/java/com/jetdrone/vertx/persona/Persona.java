package com.jetdrone.vertx.persona;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.engine.StringPlaceholderEngine;
import com.jetdrone.vertx.yoke.middleware.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.*;
import io.vertx.core.logging.impl.LoggerFactory;
import org.jetbrains.annotations.NotNull;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

import javax.crypto.Mac;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Persona extends AbstractVerticle {

    private final io.vertx.core.logging.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void start() {
        final Yoke yoke = new Yoke(vertx);
        yoke.secretSecurity("keyboard cat");

        yoke.engine("shtml", new StringPlaceholderEngine("views"));

        final Mac secret = yoke.security().getMac("HmacSHA256");

        // all environments
        yoke.use(new CookieParser(secret));
        yoke.use(new Session(secret));
        yoke.use(new BodyParser());
        yoke.use(new Static("static"));
        yoke.use(new ErrorHandler(true));

        // routes
        yoke.use(new Router()
                .get("/", (request, next) -> {
                    JsonObject sessionData = request.get("session");

                    if (sessionData == null) {
                        // no session
                        request.put("email", "null");
                    } else {
                        String email = sessionData.getString("email");

                        if (email == null) {
                            request.put("email", "null");
                        } else {
                            request.put("email", "'" + email + "'");
                        }
                    }

                    request.response().render("index.shtml", next);
                })
                .post("/auth/logout", (request, next) -> {
                    // destroy session
                    request.destroySession();
                    // send OK
                    request.response().end(new JsonObject().putBoolean("success", true));
                })
                .post("/auth/login", (request, next) -> {
                    String data;

                    try {
                        // generate the data
                        data = "assertion=" + URLEncoder.encode(request.formAttributes().get("assertion"), "UTF-8") +
                                "&audience=" + URLEncoder.encode("http://localhost:8080", "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        next.handle(e);
                        return;
                    }

                    HttpClient client = getVertx().createHttpClient(new HttpClientOptions().setSsl(true));

                    HttpClientRequest clientRequest = client.request(HttpMethod.POST, 443, "verifier.login.persona.org", "/verify", new Handler<HttpClientResponse>() {
                        public void handle(HttpClientResponse response) {
                            // error handler
                            response.exceptionHandler(err -> next.handle(err));

                            final Buffer body = Buffer.buffer(0);

                            // body handler
                            response.handler(buffer -> body.appendBuffer(buffer));
                            // done
                            response.endHandler(event -> {
                                try {
                                    JsonObject verifierResp = new JsonObject(body.toString());
                                    boolean valid = "okay".equals(verifierResp.getString("status"));
                                    String email = valid ? verifierResp.getString("email") : null;
                                    // assertion is valid:
                                    if (valid) {
                                        // generate a session
                                        request.createSession();
                                        // OK response
                                        request.response().end(new JsonObject().putBoolean("success", true));
                                    } else {
                                        request.response().end(new JsonObject().putBoolean("success", false));
                                    }
                                } catch (DecodeException ex) {
                                    // bogus response from verifier!
                                    request.response().end(new JsonObject().putBoolean("success", false));
                                }
                            });
                        }
                    });

                    clientRequest.putHeader("content-type", "application/x-www-form-urlencoded");
                    clientRequest.putHeader("content-length", Integer.toString(data.length()));
                    clientRequest.end(data);
                })
        );

        yoke.listen(8080);
        logger.info("Yoke server listening on port 8080");
    }
}
