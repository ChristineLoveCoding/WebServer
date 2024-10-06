package tests.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import tests.helpers.CreateWebSite;
import tests.helpers.RequestReader;
import tests.helpers.RequestSender;
import server.WebServer;

public class TestDelete {
    @Test
    public void testPutDocument() throws Exception {
        String testContent = "delete me pretty plox";

        String docRoot = CreateWebSite.createDocumentRoot();
        CreateWebSite.addFileToDocumentRoot(Path.of(docRoot, "deldir", "test-delete.txt"), testContent);

        try (WebServer server = new WebServer(9753, docRoot)) {
            server.listen();

            InputStream is = RequestSender.sendRequest(9753, "DELETE", "/deldir/test-delete.txt",
                    new String[] {}, null);
            RequestReader reader = new RequestReader(is);

            assertEquals("HTTP/1.1 204 No Content", reader.getFirstLine());

            assertFalse(Files.exists(Path.of(docRoot, "deldir", "test-delete.txt")));

            server.stop();
        }
    }
}
