package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import server.config.MimeTypes;

public class WebServer implements AutoCloseable {

    private static final Logger logger = Logger.getLogger(WebServer.class.getName());

    private ServerSocket serverSocket;
    private boolean running;
    private String documentRoot;
    private MimeTypes mimeTypes;

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

    public WebServer(int port, String documentRoot) throws IOException {
        this.documentRoot = documentRoot;
        this.serverSocket = new ServerSocket(port);  // Initialize the ServerSocket
        this.running = true;
        this.mimeTypes = MimeTypes.getDefault();  // Initialize MIME types with default
        logger.info("WebServer started on port " + port);
    }

    /**
     * 
     * Example of mimeTypeFileContent: html htm text/html\npng image/png\njpg
     * image/jpeg\ngif image/gif\n
     */


    /**
     * After the webserver instance is constructed, this method will be
     * called to begin listening for requestd
     */
    public void listen() {

        while (this.running) {
            try {
                Socket clientSocket = serverSocket.accept();  // Accept incoming connections
                new Thread(new ClientHandler(clientSocket, documentRoot, mimeTypes)).start();  // Handle each request in a new thread
            } catch (IOException e) {
                logger.severe("Error accepting connection: " + e.getMessage());
            }
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