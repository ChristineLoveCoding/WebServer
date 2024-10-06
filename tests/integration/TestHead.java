package tests.integration;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import tests.helpers.CreateWebSite;
import tests.helpers.RequestReader;
import tests.helpers.RequestSender;
import server.WebServer;

public class TestHead {
    @Test
    public void testHeadExistingDocument() throws Exception {
        String testContent = "hello world 42kadflk";

        String docRoot = CreateWebSite.createDocumentRoot();
        CreateWebSite.addFileToDocumentRoot(Path.of(docRoot, "test.txt"), testContent);

        try (WebServer server = new WebServer(9753, docRoot)) {
            server.listen();

            InputStream is = RequestSender.sendRequest(9753, "HEAD", "/test.txt", new String[] {}, null);
            RequestReader reader = new RequestReader(is);

            assertEquals("HTTP/1.1 200 Ok", reader.getFirstLine());
            assertEquals("text/plain", reader.getContentType());

            assertNull(reader.getBody());

            server.stop();
        }
    }
}
