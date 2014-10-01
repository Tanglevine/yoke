package com.jetdrone.vertx.yoke.test;

import com.jetdrone.vertx.yoke.AbstractMiddleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.testComplete;

public class MiddlewareTest extends TestVerticle {

    @Test
    public void testMiddleware() {
        final Yoke yoke = new Yoke(this);

        yoke.use(new AbstractMiddleware() {
            @Override
            public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                assertNotNull(this.yoke);
                testComplete();
            }
        });

        new YokeTester(yoke).request("GET", "/", null);
    }

    @Test
    public void testXml() throws TransformerException, XMLStreamException {
        String message = "\n" +
                "\n" +
                "<Customers>\n" +
                "    <Customer Id=\"99\">\n" +
                "        <Name>Bob</Name>\n" +
                "        <Age>39</Age>\n" +
                "        <Address>\n" +
                "            <Street>10 Idle Lane</Street>\n" +
                "            <City>Yucksville</City>\n" +
                "            <PostalCode>xxxyyy</PostalCode>\n" +
                "        </Address>\n" +
                "    </Customer>\n" +
                "    <Customer Id=\"101\">\n" +
                "        <Name>Bill</Name>\n" +
                "        <Age>39</Age>\n" +
                "        <LastName/>\n" +
                "        <Address>\n" +
                "            <Street>10 Idle Lane</Street>\n" +
                "            <City>Yucksville</City>\n" +
                "            <PostalCode>xxxyyy</PostalCode>\n" +
                "        </Address>\n" +
                "    </Customer>\n" +
                "\n" +
                "</Customers>\n" +
                "\n";

        JsonObject json = Utils.xmlToJson(message).getObject("Customers");
        assertNotNull(json);

        String xml = Utils.jsonToXml(json, "Customers");
        assertNotNull(xml);

        testComplete();
    }
}
