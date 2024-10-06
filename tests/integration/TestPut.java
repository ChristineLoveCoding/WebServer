package tests.integration;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import tests.helpers.CreateWebSite;
import tests.helpers.RequestReader;
import tests.helpers.RequestSender;

import server.WebServer;

public class TestPut {
    @Test
    public void testPutDocument() throws Exception {
        String testContent = "this is our test content";

        String docRoot = CreateWebSite.createDocumentRoot();

        try (WebServer server = new WebServer(9753, docRoot)) {
            server.listen();

            InputStream is = RequestSender.sendRequest(9753, "PUT", "/putdir/test.txt",
                    new String[] { String.format("Content-Length: %d", testContent.length()) }, testContent);
            RequestReader reader = new RequestReader(is);

            assertEquals("HTTP/1.1 201 Created", reader.getFirstLine());
            Path createdFilePath = Path.of(docRoot, "putdir", "test.txt");

            assertTrue(Files.exists(createdFilePath));

            String content = Files.readString(createdFilePath);
            assertEquals(testContent, content);

            server.stop();
        }
    }
}
