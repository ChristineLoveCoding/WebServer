package tests.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.junit.jupiter.api.Test;

import tests.helpers.CreateWebSite;
import tests.helpers.RequestReader;
import server.WebServer;

public class TestBadRequest {

    @Test
    public void testBadRequest() throws Exception {
        String docRoot = CreateWebSite.createDocumentRoot();

        try (WebServer server = new WebServer(9753, docRoot)) {
            server.listen();

            try (Socket socket = new Socket("localhost", 9753)) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                writer.write(String.format("GET HTTP/1.1\r\n\r\n"));
                writer.flush();

                RequestReader reader = new RequestReader(socket.getInputStream());
                assertEquals("HTTP/1.1 400 Bad Request", reader.getFirstLine());

                server.stop();
            }
        }
    }
}
