
package com.documind.infrastructure.vectorstore.pgvector;

import com.documind.application.port.out.VectorStorePort;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class PgVectorStoreAdapter implements VectorStorePort {

    private final VectorStore vectorStore;

    public PgVectorStoreAdapter(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void save(UUID documentId, int chunkIndex, String content) {

        Document document = new Document(
                content,
                Map.of(
                        "documentId", documentId.toString(),
                        "chunkIndex", chunkIndex
                )
        );

        vectorStore.add(List.of(document));
    }

    @Override
    public List<String> searchSimilar(
            UUID documentId,
            String query,
            int topK) {

        if (documentId == null) {
            throw new IllegalArgumentException("Document ID is required");
        }

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }

        if (topK <= 0) {
            throw new IllegalArgumentException("topK must be positive");
        }

        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .filterExpression(
                        "documentId == '" + documentId + "'"
                )
                .build();

        return vectorStore.similaritySearch(searchRequest)
                .stream()
                .map(Document::getText)
                .toList();
    }

    @Override
    public void deleteByDocumentId(UUID documentId) {

        vectorStore.delete(
                "documentId == '" + documentId + "'"
        );
    }
}
