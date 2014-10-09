package com.jetdrone.vertx.yoke.test;

import com.jetdrone.vertx.yoke.middleware.AbstractMiddleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.util.Utils;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

public class MiddlewareTest extends VertxTestBase {

    @Test
    public void testMiddleware() {
        final Yoke yoke = new Yoke(vertx);

        yoke.use(new AbstractMiddleware() {
            @Override
            public void handle(@NotNull YokeRequest request, @NotNull Handler<Object> next) {
                assertNotNull(this.yoke);
                testComplete();
            }
        });

        new YokeTester(yoke).request(HttpMethod.GET, "/", null);
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
