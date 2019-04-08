package org.icoder.http.handlers;

import org.icoder.http.Request;
import org.icoder.http.responses.Response;

/**
 * @author igorzg on 2019-04-07.
 * @since 1.0
 */
public interface Handler {
    /**
     * Handle httpServer
     */
     Response handle(Request request);
}
