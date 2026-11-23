
package com.documind.application.port.in;

import com.documind.domain.model.DocumentChunk;

import java.util.List;
import java.util.UUID;

public interface ProcessDocumentUseCase {

    List<DocumentChunk> processDocument(UUID documentId);
}
