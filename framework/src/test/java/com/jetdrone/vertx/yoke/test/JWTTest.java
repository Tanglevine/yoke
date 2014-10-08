package com.jetdrone.vertx.yoke.test;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.security.JWT;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class JWTTest extends VertxTestBase {

    @Test
    public void testJWT() {
        final Yoke yoke = new Yoke(vertx);
        yoke.secretSecurity("keyboard cat");

        JWT jwt = new JWT(yoke.security());
        testComplete();
    }

    @Test
    public void testJWT2() {
        Yoke yoke = new Yoke(vertx);
        yoke.secretSecurity("keyboard cat");

        JWT jwt = new JWT(yoke.security());

        long now = System.currentTimeMillis();

        JsonObject json = new JsonObject()
                .putString("name", "Paulo Lopes")
                .putNumber("uid", 0)
                .putNumber("iat", now)
                .putNumber("exp", now + 24*60*60*1000)
                .putArray("claims", new JsonArray().add("a").add("b"));

        String token = jwt.encode(json);

        assertTrue(!token.contains("\n"));

        JsonObject decoded = jwt.decode(token);

        assertEquals("Paulo Lopes", decoded.getString("name"));
        assertEquals(0, decoded.getNumber("uid"));
        assertEquals(now, decoded.getNumber("iat"));
        assertEquals(now + 24*60*60*1000, decoded.getNumber("exp"));

        testComplete();
    }
}
