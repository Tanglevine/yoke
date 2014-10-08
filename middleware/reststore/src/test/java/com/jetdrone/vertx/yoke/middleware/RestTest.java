package com.jetdrone.vertx.yoke.middleware;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.rest.Store;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class RestTest extends VertxTestBase {

    private Store dummyStore = new Store() {
        @Override
        public void create(String entity, JsonObject object, AsyncResultHandler<String> response) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void read(String entity, String id, AsyncResultHandler<JsonObject> response) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void update(String entity, String id, JsonObject object, AsyncResultHandler<Number> response) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void delete(String entity, String id, AsyncResultHandler<Number> response) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void query(String entity, JsonObject query, Number start, Number end, JsonObject sort, AsyncResultHandler<JsonArray> response) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void count(String entity, JsonObject query, AsyncResultHandler<Number> response) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    @Test
    public void restTest() {

        Yoke yoke = new Yoke(vertx);
        JsonRestRouter restStore = new JsonRestRouter(dummyStore);
        restStore.rest("/persons", "persons");
        yoke.use(restStore);

        testComplete();
    }
}
