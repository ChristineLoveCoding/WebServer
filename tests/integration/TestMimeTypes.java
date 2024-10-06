package tests.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import tests.helpers.CreateWebSite;
import tests.helpers.RequestReader;
import tests.helpers.RequestSender;
import server.WebServer;

public class TestMimeTypes {

    @ParameterizedTest
    @MethodSource("provideMimeTypes")
    public void testMimeTypes(String expectedMimeType, String fileName) throws Exception {
        String testContent = "the server doesnt care what the content of a file is";

        String docRoot = CreateWebSite.createDocumentRoot();
        CreateWebSite.addFileToDocumentRoot(Path.of(docRoot, "mime-test", fileName), testContent);

        try (WebServer server = new WebServer(9753, docRoot)) {
            server.listen();

            InputStream is = RequestSender.sendRequest(9753, "GET", String.format("/mime-test/%s", fileName),
                    new String[] {}, null);
            RequestReader reader = new RequestReader(is);

            assertEquals("HTTP/1.1 200 Ok", reader.getFirstLine());
            assertEquals(expectedMimeType, reader.getContentType());

            server.stop();
        }
    }

    private static Stream<Arguments> provideMimeTypes() {
        return Stream.of(
                Arguments.of("text/html", "html-file.html"),
                Arguments.of("text/plain", "text-file.txt"),
                Arguments.of("text/css", "style-file.css"),
                Arguments.of("text/javascript", "js-file.js"),
                Arguments.of("image/jpeg", "image.jpg"),
                Arguments.of("image/png", "image.png"),
                Arguments.of("image/gif", "image.gif"),
                Arguments.of("image/svg+xml", "image.svg"),
                Arguments.of("application/pdf", "document.pdf"),
                Arguments.of("application/json", "json-file.json"));
    }
}
