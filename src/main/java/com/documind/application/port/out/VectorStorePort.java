
package com.documind.application.port.out;

import java.util.List;
import java.util.UUID;

public interface VectorStorePort {

    void save(
            UUID documentId,
            int chunkIndex,
            String content
    );

    List<String> searchSimilar(
            UUID documentId,
            String query,
            int topK
    );

    void deleteByDocumentId(UUID documentId);
}


