
package com.documind.application.service;

import com.documind.application.port.in.DocumentUseCase;
import com.documind.application.port.out.DocumentPersistencePort;
import com.documind.domain.model.Document;
import org.springframework.stereotype.Service;
import com.documind.domain.exception.DocumentNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentService implements DocumentUseCase {

    private final DocumentPersistencePort documentPersistencePort;

    public DocumentService(
            DocumentPersistencePort documentPersistencePort
    ) {
        this.documentPersistencePort = documentPersistencePort;
    }

    @Override
    public Document saveDocument(Document document) {
        return documentPersistencePort.save(document);
    }

    @Override
    public Document getDocumentById(UUID id) {
        return documentPersistencePort.findById(id).orElseThrow(()->
                new DocumentNotFoundException("Document with id: " + id + " not found"));

    }

    @Override
    public List<Document> getAllDocuments() {
        return documentPersistencePort.findAll();
    }

    @Override
    public void deleteDocument(UUID id) {
        getDocumentById(id);
        documentPersistencePort.deleteById(id);
    }
}
