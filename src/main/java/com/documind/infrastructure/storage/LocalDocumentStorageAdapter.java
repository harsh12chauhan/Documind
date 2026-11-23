
package com.documind.infrastructure.storage;

import com.documind.application.port.out.DocumentStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Component
public class LocalDocumentStorageAdapter implements DocumentStoragePort {

    private final Path storageLocation;

    public LocalDocumentStorageAdapter(
            @Value("${documind.storage.location:uploads}")
            String storageLocation
    ) {
        this.storageLocation = Path.of(storageLocation)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public String store(
            String fileName,
            String contentType,
            byte[] content
    ) {
        try {
            Files.createDirectories(storageLocation);

            String safeFileName = Path.of(fileName)
                    .getFileName()
                    .toString()
                    .replaceAll("[^a-zA-Z0-9._-]", "_");

            String storedFileName =
                    UUID.randomUUID() + "_" + safeFileName;

            Path targetPath = storageLocation.resolve(storedFileName)
                    .normalize();

            if (!targetPath.startsWith(storageLocation)) {
                throw new IllegalArgumentException(
                        "Invalid file path"
                );
            }

            Files.write(
                    targetPath,
                    content,
                    StandardOpenOption.CREATE_NEW
            );

            return storedFileName;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to store document",
                    exception
            );
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            Path targetPath = storageLocation.resolve(filePath)
                    .normalize();

            if (!targetPath.startsWith(storageLocation)) {
                throw new IllegalArgumentException(
                        "Invalid file path"
                );
            }

            Files.deleteIfExists(targetPath);

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to delete document",
                    exception
            );
        }
    }

    @Override
    public byte[] read(String filePath) {

        Path targetPath = storageLocation.resolve(filePath).normalize();

        if (!targetPath.startsWith(storageLocation)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        if (!Files.isRegularFile(targetPath)) {
            throw new IllegalArgumentException("Stored document not found: " + filePath);
        }

        try {
            return Files.readAllBytes(targetPath);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to read stored document: " + filePath,
                    exception
            );
        }
    }

}
