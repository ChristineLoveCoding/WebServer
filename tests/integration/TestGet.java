package tests.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import server.WebServer;
import tests.helpers.CreateWebSite;
import tests.helpers.RequestReader;
import tests.helpers.RequestSender;

public class TestGet {

    @Test
    public void testGetExistingDocument() throws Exception {
        String testContent = "hello world 42kadflk";

        String docRoot = CreateWebSite.createDocumentRoot();
        CreateWebSite.addFileToDocumentRoot(Path.of(docRoot, "test.txt"), testContent);

        try (WebServer server = new WebServer(9753, docRoot)) {
            server.listen();

            InputStream is = RequestSender.sendRequest(9753, "GET", "/test.txt", new String[] {}, null);
            RequestReader reader = new RequestReader(is);

            assertEquals("HTTP/1.1 200 Ok", reader.getFirstLine());
            assertEquals("text/plain", reader.getContentType());
            assertEquals(testContent.length(), reader.getContentLength());

            byte[] body = reader.getBody();

            assertNotNull(body);
            for (int index = 0; index < testContent.length(); index++) {
                assertEquals(testContent.charAt(index), body[index]);
            }

            server.stop();
        }
    }

    @Test
    public void testGetNonExistingDocument() throws Exception {
        String docRoot = CreateWebSite.createDocumentRoot();

        try (WebServer server = new WebServer(9753, docRoot)) {
            server.listen();

            InputStream is = RequestSender.sendRequest(9753, "GET", "/non-existing.txt", new String[] {}, null);
            RequestReader reader = new RequestReader(is);

            assertEquals("HTTP/1.1 404 Not Found", reader.getFirstLine());

            server.stop();
        }
    }
}
