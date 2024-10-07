package server;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.logging.Logger;

import server.config.MimeTypes;

class ClientHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private Socket clientSocket;
    private String documentRoot;
    private MimeTypes mimeTypes;

    public ClientHandler(Socket clientSocket, String documentRoot, MimeTypes mimeTypes) {
        this.clientSocket = clientSocket;
        this.documentRoot = documentRoot;
        this.mimeTypes = mimeTypes;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            String requestLine = in.readLine();  // Read the request line (e.g., GET /test.txt HTTP/1.1)
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            logger.info("Received request: " + requestLine);

            // Parse the request
            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String filePath = requestParts[1].equals("/") ? "/index.html" : requestParts[1];
            Path file = Paths.get(documentRoot, filePath);

            if ("GET".equalsIgnoreCase(method)) {
                handleGet(file, out);
            } else {
                sendResponse(out, 405, "Method Not Allowed", "Unsupported method: " + method);
            }

        } catch (IOException e) {
            logger.severe("Error handling client request: " + e.getMessage());
        }
    }

    private void handleGet(Path file, OutputStream out) throws IOException {
        if (Files.exists(file) && !Files.isDirectory(file)) {
            String contentType = mimeTypes.getDefault().getMimeTypeFromExtension(Files.probeContentType(file));
            byte[] content = Files.readAllBytes(file);
            sendResponse(out, 200, "OK", content, contentType);
        } else {
            sendResponse(out, 404, "Not Found", "File not found.");
        }
    }

    private void sendResponse(OutputStream out, int statusCode, String statusText, String body) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" + body;
        out.write(response.getBytes());
    }

    private void sendResponse(OutputStream out, int statusCode, String statusText, byte[] body, String contentType) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "\r\n";
        out.write(response.getBytes());
        out.write(body);
    }
}

