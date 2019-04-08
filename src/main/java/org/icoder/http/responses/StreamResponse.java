package org.icoder.http.responses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author igorzg on 2019-04-07.
 * @since 1.0
 */
public class StreamResponse extends Response {

    private InputStream inputStream;

    public StreamResponse(int statusCode, InputStream inputStream) {
        super(statusCode);
        this.inputStream = inputStream;
    }

    public StreamResponse(int statusCode, String content) {
        super(statusCode);
        this.inputStream = new ByteArrayInputStream(content.getBytes());
    }

    public StreamResponse(int statusCode) {
        super(statusCode);
        this.inputStream = new ByteArrayInputStream("".getBytes());
    }

    @Override
    protected void write(BufferedWriter writer) throws IOException {
        if (inputStream != null) {
            if (!headers.containsKey("Content-Type")) {
                headers.put("Content-Type", "application/octet-stream");
            }
            write(inputStream, writer);
        }
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(StreamResponse.class);
    }
}
