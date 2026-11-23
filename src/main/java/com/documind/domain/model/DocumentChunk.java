
package com.documind.domain.model;

import java.util.UUID;

public record DocumentChunk(
        UUID documentId,
        int chunkIndex,
        String content
) {
    public DocumentChunk {
        if (documentId == null) {
            throw new IllegalArgumentException("Document ID is required");
        }

        if (chunkIndex < 0) {
            throw new IllegalArgumentException("Chunk index cannot be negative");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Chunk content cannot be empty");
        }
    }
}
