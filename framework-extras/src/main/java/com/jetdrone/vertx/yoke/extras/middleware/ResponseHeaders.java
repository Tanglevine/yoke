
package com.jetdrone.vertx.yoke.extras.middleware;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jetdrone.vertx.yoke.AbstractMiddleware;
import org.vertx.java.core.Handler;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.middleware.YokeResponse;

/**
 * <p>
 * A simple {@link Middleware} that allows adding custom response headers to all
 * {@link YokeResponse}.
 * </p>
 * <p>
 * Example:
 * </p>
 *
 * <pre>
 * {@code
 * yoke.use(new ResponseHeaders()
 *          .with("X-Build-Meta", "1.0-SNAPSHOT")
 *          .with("X-Server-Id", "server-123"));
 * }
 * </pre>
 */
public class ResponseHeaders extends AbstractMiddleware
{
    private final Map<CharSequence, CharSequence[]> headers;

    public ResponseHeaders()
    {
        headers = new HashMap<>();
    }

    public ResponseHeaders with(final CharSequence name, final CharSequence... values)
    {
        headers.put(name, values);
        return this;
    }

    @Override
    public void handle(final YokeRequest request, final Handler<Object> next)
    {
        for (final Entry<CharSequence, CharSequence[]> header : headers.entrySet())
        {
            request.response().headers().add(header.getKey(), Arrays.asList(header.getValue()));
        }

        next.handle(null);
    }
}
