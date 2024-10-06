package tests.helpers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class RequestSender {

    public static InputStream sendRequest(int port, String method, String resourceUri, String[] headers, String body)
            throws IOException {
        try (Socket socket = new Socket("localhost", port)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write(String.format("%s %s HTTP/1.1\r\n", method, resourceUri));

            for (String header : headers) {
                writer.write(String.format("%s\r\n", header));
            }

            writer.write("\r\n");

            if (body != null) {
                writer.write(body);
            }
            writer.flush();

            return socket.getInputStream();
        }
    }
}
