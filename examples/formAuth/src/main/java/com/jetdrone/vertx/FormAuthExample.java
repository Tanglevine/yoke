package com.jetdrone.vertx;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.AuthHandler;
import com.jetdrone.vertx.yoke.middleware.FormAuth;
import com.jetdrone.vertx.yoke.middleware.*;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import javax.crypto.Mac;

public class FormAuthExample extends Verticle {

    @Override
    public void start() {

        final Yoke app = new Yoke(this);
        app.secretSecurity("keyboard cat");

        final Mac hmac = app.security().getMac("HmacSHA256");

        app.use(new BodyParser());
        app.use(new CookieParser(hmac));
        app.use(new Session(hmac));

        final FormAuth formAuth = new FormAuth((username, password, result) -> {
            if ("foo".equals(username) && "bar".equals(password)) {
                result.handle(new JsonObject().putString("username", "foo"));
            } else {
                result.handle(null);
            }
        });

        app.use(formAuth);

        app.use(new Router() {{
            get("/", (request, next) -> {
                JsonObject session = request.get("session");

                if (session != null && session.getString("user") != null) {
                    request.response().setContentType("text/html");
                    request.response().end("Welcome " + session.getString("user") + "<br>" + "<a href='/logout'>logout</a>");
                } else {
                    request.response().end("<a href='/login'> Login</a>");
                }
            });

            get("/profile", formAuth.RequiredAuth, (request, next) -> {
                JsonObject session = request.get("session");
                request.response().setContentType("text/html");
                request.response().end("Profile page of " + session.getString("user") + "<br>" + " click to <a href='/logout'>logout</a>");
            });
        }});

        app.listen(8000);
    }
}