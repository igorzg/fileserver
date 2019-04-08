package org.icoder.http;

import java.io.InputStream;
import java.util.HashMap;

/**
 * @author igorzg on 2019-04-07.
 * @since 1.0
 */
public class Request {

    private String requestLine;
    private HashMap<String, String> headers;
    private HashMap<String, String> params;
    private String method;
    private String uri;
    private String version;
    private String path;
    private String query;
    private InputStream inputStream;
    private boolean keepAlive;

    public Request(
            String requestLine,
            HashMap<String, String> headers,
            HashMap<String, String> params,
            String method,
            String uri,
            String version,
            String path,
            String query,
            InputStream inputStream,
            boolean keepAlive
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.params = params;
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.path = path;
        this.query = query;
        this.inputStream = inputStream;
        this.keepAlive = keepAlive;
    }

    public String getRequestLine() {
        return requestLine;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }
}
