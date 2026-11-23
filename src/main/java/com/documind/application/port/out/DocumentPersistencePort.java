
package com.documind.application.port.out;

import com.documind.domain.model.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentPersistencePort {

    Document save(Document document);

    Optional<Document> findById(UUID id);

    List<Document> findAll();

    void deleteById(UUID id);
}
