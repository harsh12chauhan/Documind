
package com.documind.application.port.out;

import com.documind.domain.model.DocumentChunk;

import java.util.List;
import java.util.UUID;

public interface DocumentChunkPersistencePort {

    void saveAll(List<DocumentChunk> chunks);

    List<DocumentChunk> findByDocumentId(UUID documentId);

    void deleteByDocumentId(UUID documentId);
}
