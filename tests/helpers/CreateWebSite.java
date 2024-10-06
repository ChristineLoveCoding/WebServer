package tests.helpers;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateWebSite {
    public static String createDocumentRoot() throws IOException {
        Path docRoot = Files.createTempDirectory("");

        return docRoot.toAbsolutePath().toString();
    }

    public static void addFileToDocumentRoot(Path filePath, String fileContent) throws IOException {
        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }

        Path file = Files.createFile(filePath);

        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write(fileContent);
        }
    }
}
