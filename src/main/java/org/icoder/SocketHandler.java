package org.icoder;

import org.icoder.http.Request;
import org.icoder.http.exceptions.BadRequestException;
import org.icoder.http.exceptions.ClosedConnectionException;
import org.icoder.http.handlers.Handler;
import org.icoder.http.responses.StreamResponse;
import org.icoder.http.responses.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @author igorzg on 2019-04-07.
 * @since 1.0
 */
public class SocketHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(SocketHandler.class);
    private Handler handler;
    private Socket connection;
    private InputStream in;
    private OutputStream out;

    /**
     * Socket handler
     *
     * @param socket  Socket
     * @param handler Handler
     */
    public SocketHandler(Socket socket, Handler handler) {
        this.connection = socket;
        this.handler = handler;
    }

    /**
     * Run
     */
    @Override
    public void run() {
        try {
            in = connection.getInputStream();
            out = connection.getOutputStream();
            Request request = parse(in);
            Response response = handler.handle(request);
            response.getHeaders().put("Server", "AlphaCoder");
            response.getHeaders().put("Date", Calendar.getInstance().getTime().toString());
//            if (request.isKeepAlive()) {
//                response.getHeaders().put("Connection", "Keep-Alive");
//            } else {
//                response.getHeaders().put("Connection", "close");
//            }
            response.getHeaders().put("Connection", "close");
            response.flush(out);
        } catch (IOException e) {
            logger.error("Error in client's IO.");
            new StreamResponse(500, "Error in client's IO.").flush(out);
        } catch (BadRequestException e) {
            logger.error("Bad Request");
            new StreamResponse(400, "Server only accepts HTTP protocol").flush(out);
        } catch (ClosedConnectionException e) {
            logger.error("Connection closed by client");
            new StreamResponse(500, "Connection closed by client").flush(out);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                logger.error("Error while closing input stream.");
            }
            try {
                out.close();
            } catch (IOException e) {
                logger.error("Error while closing output stream.");
            }
            try {
                connection.close();
            } catch (IOException e) {
                logger.error("Error while closing client socket.");
            }
        }
    }

    /**
     * Parse request
     *
     * @param in InputStream
     * @return Request
     * @throws BadRequestException
     * @throws ClosedConnectionException
     */
    private Request parse(InputStream in) throws BadRequestException, ClosedConnectionException {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String readLine = reader.readLine();
            if (readLine == null) {
                throw new ClosedConnectionException();
            }

            String[] requestLineParts = readLine.split(" ", 3);

            String method = requestLineParts[0];
            String uri = requestLineParts[1];
            String version = requestLineParts[2];

            HashMap<String, String> headers = new HashMap<>();
            HashMap<String, String> params = new HashMap<>();

            String line = reader.readLine();
            while (!line.isEmpty()) {
                String[] lineParts = line.split(":", 2);
                if (lineParts.length == 2) {
                    headers.put(lineParts[0], lineParts[1].trim());
                }
                line = reader.readLine();
            }
            boolean keepAlive = "keep-alive".equals(headers.getOrDefault("Connection", "close"));
            String[] uriParts = uri.split("\\?", 2);

            String path = uri;
            String query = "";

            if (uriParts.length == 2) {
                path = uriParts[0];
                query = uriParts[1];
                String[] keyValuePairs = query.split("&");
                for (String keyValuePair : keyValuePairs) {
                    String[] keyValue = keyValuePair.split("=", 2);
                    if (keyValue.length == 2) {
                        params.put(keyValue[0], keyValue[1]);
                    }
                }
            }
            return new Request(readLine, headers, params, method, uri, version, path, query, in, keepAlive);
        } catch (ClosedConnectionException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException();
        }
    }
}
