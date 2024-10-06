package server;

import java.net.ServerSocket;

import server.config.MimeTypes;

public class WebServer implements AutoCloseable {

    private ServerSocket serverSocket;
    private boolean running;

    public static void main(String[] args) throws NumberFormatException, Exception {
        if (args.length != 2) {
            System.err.println("usage: java WebServer <port number> <document root>");
            System.exit(1);
        }

        try (WebServer server = new WebServer(
                Integer.parseInt(args[0]),
                args[1])) {
            server.listen();
        }
    }

    public WebServer(int port, String documentRoot) {
        this.running = true;
    }

    /**
     * 
     * Example of mimeTypeFileContent: html htm text/html\npng image/png\njpg
     * image/jpeg\ngif image/gif\n
     */
    public WebServer(int port, String documentRoot, String mimeTypeFileContent) {
        this.running = true;

        MimeTypes mimeTypes = MimeTypes.getDefault();

        // Parse the mimeTypesFileContent and add the mime types to the mimeTypes object
        mimeTypeFileContent.lines().forEach(line -> {
            String[] parts = line.split("\\s+");

            for (int index = 0; index < parts.length - 1; index++) {
                mimeTypes.addMimeType(parts[index], parts[parts.length - 1]);
            }
        });

    }

    /**
     * After the webserver instance is constructed, this method will be
     * called to begin listening for requestd
     */
    public void listen() {

        // Feel free to change this logic
        while (this.running) {
            // Handle a request
        }
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void close() throws Exception {
        this.serverSocket.close();
    }
}