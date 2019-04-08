import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.icoder.Application;
import org.icoder.http.handlers.StaticFileHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author igorzg on 2019-04-07.
 * @since 1.0
 */
@RunWith(JUnitPlatform.class)
public class ApplicationTest {

    private String HOST = "http://127.0.0.1:5000/";
    private static Thread server;
    private static CloseableHttpClient client;

    @BeforeAll
    static void setUp() {
        client = HttpClients.createDefault();
        server = new Thread(() -> new Application(
                5000,
                4,
                new StaticFileHandler("public", "index.html")
        ));
        server.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        client.close();
        server.interrupt();
    }


    @Test
    void testServer() throws IOException {
        HttpGet httpGet = new HttpGet(HOST);
        CloseableHttpResponse response = client.execute(httpGet);
        assertEquals(response.getStatusLine().getStatusCode(), 200);
        response.close();
    }

    @Test
    void testHeadServer() throws IOException {
        HttpHead httpHead = new HttpHead(HOST);
        CloseableHttpResponse response = client.execute(httpHead);
        assertEquals(response.getStatusLine().getStatusCode(), 200);
        assertThrows(NullPointerException.class, () -> response.getEntity().getContent());
        assertEquals(response.getFirstHeader("Content-Length").getValue(), "132");
        response.close();
    }

    @Test
    void testTraceServer() throws IOException {
        HttpTrace httpTrace = new HttpTrace(HOST);
        CloseableHttpResponse response = client.execute(httpTrace);
        String data = EntityUtils.toString(response.getEntity());
        assertEquals(data, "Method not implemented!");
        assertEquals(response.getStatusLine().getStatusCode(), 405);
        response.close();
    }

    @Test
    void testFailed() throws IOException {
        HttpGet httpGet = new HttpGet(HOST + "/unknown");
        CloseableHttpResponse response = client.execute(httpGet);
        assertEquals(response.getStatusLine().getStatusCode(), 404);
        response.close();
    }

}
