package tests.integration;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import tests.helpers.CreateWebSite;
import tests.helpers.RequestReader;
import tests.helpers.RequestSender;
import server.WebServer;

public class TestAuthentication {

    private void createPasswordFile(Path fileLocation, String username, String password) throws IOException {
        Files.write(fileLocation, String.format("%s:%s", username, password).getBytes());
    }

    @Test
    public void testAuthentication401() throws Exception {
        String testContent = "i am secret";

        String docRoot = CreateWebSite.createDocumentRoot();

        Path protectedPath = Path.of(docRoot, "top-secret");

        CreateWebSite.addFileToDocumentRoot(Path.of(protectedPath.toString(), "file.html"), testContent);
        createPasswordFile(Path.of(protectedPath.toString(), ".password"), "admin", "admin");

        try (WebServer server = new WebServer(9753, docRoot)) {
            server.listen();

            InputStream is = RequestSender.sendRequest(9753, "GET", String.format("/top-secret/file.html"),
                    new String[] {}, null);
            RequestReader reader = new RequestReader(is);

            assertEquals("HTTP/1.1 401 Unauthorized", reader.getFirstLine());
            assertTrue(reader.getHeaders().contains("WWW-Authenticate: Basic realm=\"667 Server\""));

            server.stop();
        }
    }

    @Test
    public void testAuthentication200() throws Exception {
        String testContent = "i am secret";

        String docRoot = CreateWebSite.createDocumentRoot();

        Path protectedPath = Path.of(docRoot, "top-secret");

        CreateWebSite.addFileToDocumentRoot(Path.of(protectedPath.toString(), "file.html"), testContent);
        createPasswordFile(Path.of(protectedPath.toString(), ".password"), "admin", "admin");

        try (WebServer server = new WebServer(9753, docRoot)) {
            server.listen();

            String credentials = new String(Base64.getEncoder().encode("admin:admin".getBytes()),
                    StandardCharsets.UTF_8);
            InputStream is = RequestSender.sendRequest(9753, "GET", String.format("/top-secret/file.html"),
                    new String[] { String.format("Authorization: %s", credentials) }, null);
            RequestReader reader = new RequestReader(is);

            assertEquals("HTTP/1.1 200 Ok", reader.getFirstLine());

            server.stop();
        }
    }

    @Test
    public void testAuthentication403() throws Exception {
        String testContent = "i am secret";

        String docRoot = CreateWebSite.createDocumentRoot();

        Path protectedPath = Path.of(docRoot, "top-secret");

        CreateWebSite.addFileToDocumentRoot(Path.of(protectedPath.toString(), "file.html"), testContent);
        createPasswordFile(Path.of(protectedPath.toString(), ".password"), "admin", "admin");

        try (WebServer server = new WebServer(9753, docRoot)) {
            server.listen();

            String credentials = new String(Base64.getEncoder().encode("sdfasdfasdf:adsadfsdafjmin".getBytes()),
                    StandardCharsets.UTF_8);
            InputStream is = RequestSender.sendRequest(9753, "GET", String.format("/top-secret/file.html"),
                    new String[] { String.format("Authorization: %s", credentials) }, null);
            RequestReader reader = new RequestReader(is);

            assertEquals("HTTP/1.1 403 Forbidden", reader.getFirstLine());

            server.stop();
        }
    }

}
