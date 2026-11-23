
package com.documind.application.port.in;

import com.documind.domain.model.Document;

import java.util.List;
import java.util.UUID;

public interface DocumentUseCase {

    Document saveDocument(Document document);

    Document getDocumentById(UUID id);

    List<Document> getAllDocuments();

    void deleteDocument(UUID id);
}
