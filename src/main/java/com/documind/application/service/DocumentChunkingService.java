
package com.documind.application.service;

import com.documind.domain.model.DocumentChunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentChunkingService {

    private final int chunkSize;
    private final int chunkOverlap;

    public DocumentChunkingService(
            @Value("${documind.chunking.size:1000}") int chunkSize,
            @Value("${documind.chunking.overlap:200}") int chunkOverlap) {

        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive");
        }

        if (chunkOverlap < 0 || chunkOverlap >= chunkSize) {
            throw new IllegalArgumentException(
                    "Chunk overlap must be non-negative and smaller than chunk size"
            );
        }

        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    public List<DocumentChunk> chunk(UUID documentId, String text) {

        if (documentId == null) {
            throw new IllegalArgumentException("Document ID is required");
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }

        List<DocumentChunk> chunks = new ArrayList<>();

        int start = 0;
        int chunkIndex = 0;

        while (start < text.length()) {

            int end = Math.min(start + chunkSize, text.length());

            if (end < text.length()) {
                int lastWhitespace = text.lastIndexOf(' ', end);

                if (lastWhitespace > start) {
                    end = lastWhitespace;
                }
            }

            String content = text.substring(start, end).trim();

            if (!content.isEmpty()) {
                chunks.add(new DocumentChunk(
                        documentId,
                        chunkIndex++,
                        content
                ));
            }

            if (end >= text.length()) {
                break;
            }

            start = end - chunkOverlap;

            if (start < 0) {
                start = 0;
            }
        }

        return List.copyOf(chunks);
    }
}
