package tests.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class RequestReader {
    private InputStream inputStream;

    private String firstLine;
    private List<String> headers;
    private byte[] body;

    private int contentLength;
    private String contentType;

    public RequestReader(InputStream stream) {
        this.inputStream = stream;

        this.process();
    }

    private void process() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.inputStream))) {
            this.firstLine = reader.readLine();

            String line = reader.readLine().trim();
            while (line.length() != 0) {
                this.headers.add(line);

                if (line.startsWith("Content-Length")) {
                    this.contentLength = Integer.parseInt(line.replace("Content-Length:", "").trim());
                } else if (line.startsWith("Content-Type")) {
                    this.contentType = line.replace("Content-Type:", "").trim();
                }

                line = reader.readLine().trim();
            }

            if (this.contentLength != 0) {
                this.inputStream.read(new byte[this.contentLength]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFirstLine() {
        return firstLine;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public String getContentType() {
        return this.contentType;
    }

    public byte[] getBody() {
        return body;
    }
}
