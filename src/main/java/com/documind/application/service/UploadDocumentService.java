
package com.documind.application.service;

import com.documind.application.port.in.UploadDocumentUseCase;
import com.documind.application.port.out.DocumentPersistencePort;
import com.documind.application.port.out.DocumentStoragePort;
import com.documind.domain.enums.DocumentStatus;
import com.documind.domain.model.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UploadDocumentService implements UploadDocumentUseCase {

    private final DocumentStoragePort documentStoragePort;
    private final DocumentPersistencePort documentPersistencePort;
    private final DocumentFileValidator documentFileValidator;

    public UploadDocumentService(
            DocumentStoragePort documentStoragePort,
            DocumentPersistencePort documentPersistencePort,
            DocumentFileValidator documentFileValidator
    ) {
        this.documentStoragePort = documentStoragePort;
        this.documentPersistencePort = documentPersistencePort;
        this.documentFileValidator = documentFileValidator;
    }

    @Override
    public Document uploadDocument(
            String fileName,
            String contentType,
            byte[] content
    ) {

        // 1. Validate uploaded document
        documentFileValidator.validate(
                fileName,
                contentType,
                content
        );

        // 2. Store physical file
        String filePath = documentStoragePort.store(
                fileName,
                contentType,
                content
        );

        // 3. Create domain document
        Document document = new Document(
                UUID.randomUUID(),
                fileName,
                contentType,
                filePath,
                content.length,
                DocumentStatus.UPLOADED,
                LocalDateTime.now()
        );

        // 4. Persist metadata
        try {
            return documentPersistencePort.save(document);

        } catch (RuntimeException exception) {

            // 5. Roll back physical file if persistence fails
            try {
                documentStoragePort.delete(filePath);
            } catch (RuntimeException cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            throw exception;
        }
    }
}
