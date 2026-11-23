
package com.documind.application.service;

import com.documind.application.port.in.ProcessDocumentUseCase;
import com.documind.application.port.out.DocumentChunkPersistencePort;
import com.documind.application.port.out.DocumentPersistencePort;
import com.documind.application.port.out.DocumentStoragePort;
import com.documind.application.port.out.DocumentTextExtractionPort;
import com.documind.application.port.out.VectorStorePort;
import com.documind.domain.exception.DocumentNotFoundException;
import com.documind.domain.model.Document;
import com.documind.domain.model.DocumentChunk;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProcessDocumentService implements ProcessDocumentUseCase {

    private final DocumentPersistencePort documentPersistencePort;
    private final DocumentStoragePort documentStoragePort;
    private final DocumentTextExtractionPort documentTextExtractionPort;
    private final DocumentChunkingService documentChunkingService;
    private final DocumentChunkPersistencePort documentChunkPersistencePort;
    private final VectorStorePort vectorStorePort;

    public ProcessDocumentService(
            DocumentPersistencePort documentPersistencePort,
            DocumentStoragePort documentStoragePort,
            DocumentTextExtractionPort documentTextExtractionPort,
            DocumentChunkingService documentChunkingService,
            DocumentChunkPersistencePort documentChunkPersistencePort,
            VectorStorePort vectorStorePort) {

        this.documentPersistencePort = documentPersistencePort;
        this.documentStoragePort = documentStoragePort;
        this.documentTextExtractionPort = documentTextExtractionPort;
        this.documentChunkingService = documentChunkingService;
        this.documentChunkPersistencePort = documentChunkPersistencePort;
        this.vectorStorePort = vectorStorePort;
    }

    @Override
    public List<DocumentChunk> processDocument(UUID documentId) {

        Document document = documentPersistencePort.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "Document not found with id: " + documentId
                ));

        byte[] content = documentStoragePort.read(document.getFilePath());

        String extractedText = documentTextExtractionPort.extractText(
                content,
                document.getFileName()
        );

        List<DocumentChunk> chunks = documentChunkingService.chunk(
                document.getId(),
                extractedText
        );

        // Remove previously stored chunks and vectors.
        documentChunkPersistencePort.deleteByDocumentId(documentId);
        vectorStorePort.deleteByDocumentId(documentId);

        // Persist chunks in PostgreSQL.
        documentChunkPersistencePort.saveAll(chunks);

        // Store chunk text and generate vectors through Spring AI.
        for (DocumentChunk chunk : chunks) {
            vectorStorePort.save(
                    chunk.documentId(),
                    chunk.chunkIndex(),
                    chunk.content()
            );
        }

        return chunks;
    }
}
