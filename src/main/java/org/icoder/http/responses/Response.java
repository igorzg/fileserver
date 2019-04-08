package org.icoder.http.responses;

import org.slf4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author igorzg on 2019-04-07.
 * @since 1.0
 */
public abstract class Response {

    protected static Map<Integer, String> responses;

    static {
        responses = new HashMap<>();
        responses.put(100, "Continue");
        responses.put(101, "Switching Protocols");
        responses.put(200, "OK");
        responses.put(201, "Created");
        responses.put(300, "Multiple Choices");
        responses.put(301, "Moved Permanently");
        responses.put(302, "Found");
        responses.put(400, "Bad Request");
        responses.put(401, "Unauthorized");
        responses.put(402, "Payment Required");
        responses.put(403, "Not Found");
        responses.put(404, "Continue");
        responses.put(405, "Method Not Allowed");
        responses.put(500, "Internal Server Error");
        responses.put(501, "Not Implemented");
        responses.put(502, "Bad Gateway");
        responses.put(503, "Service Unavailable");
        responses.put(504, "Gateway Time-out");
        responses.put(505, "HTTP Version not supported");
    }

    protected String protocol = "HTTP/1.1";

    protected int statusCode;

    protected HashMap<String, String> headers = new HashMap<>();

    public Response(int statusCode) {
        this.statusCode = statusCode;
    }

    abstract protected void write(BufferedWriter writer) throws IOException;

    abstract public Logger getLogger();

    /**
     * Flush data to client
     *
     * @param out OutputStream
     */
    public void flush(OutputStream out) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out));
            bufferedWriter.write(getResponseLine());
            bufferedWriter.write("\r\n");
            for (String key : headers.keySet()) {
                bufferedWriter.write(key + ":" + headers.get(key));
                bufferedWriter.write("\r\n");
            }
            bufferedWriter.write("\r\n");
            write(bufferedWriter);
            bufferedWriter.flush();
        } catch (IOException e) {
            getLogger().error("Error writing to output", e);
        }
    }

    /**
     * Write from input stream
     *
     * @param inputStream    InputStream
     * @param bufferedWriter BufferedWriter
     */
    protected void write(InputStream inputStream, BufferedWriter bufferedWriter) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        char[] buffer = new char[1024];
        int read;
        while ((read = reader.read(buffer)) != -1) {
            bufferedWriter.write(buffer, 0, read);
        }
        reader.close();
    }

    /**
     * Get response line
     *
     * @return
     */
    private String getResponseLine() {
        return protocol
                + " "
                + String.valueOf(statusCode)
                + " "
                + responses.getOrDefault(statusCode, "Not Specified");
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }
}
