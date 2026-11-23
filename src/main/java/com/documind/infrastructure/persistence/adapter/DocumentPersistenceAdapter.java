
package com.documind.infrastructure.persistence.adapter;

import com.documind.domain.model.Document;
import com.documind.application.port.out.DocumentPersistencePort;
import com.documind.infrastructure.persistence.entity.DocumentEntity;
import com.documind.infrastructure.persistence.repository.DocumentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DocumentPersistenceAdapter implements DocumentPersistencePort {

    private final DocumentJpaRepository documentJpaRepository;

    public DocumentPersistenceAdapter(
            DocumentJpaRepository documentJpaRepository
    ) {
        this.documentJpaRepository = documentJpaRepository;
    }

    @Override
    public Document save(Document document) {
        DocumentEntity entity = toEntity(document);

        DocumentEntity savedEntity =
                documentJpaRepository.save(entity);

        return toDomain(savedEntity);
    }

    @Override
    public Optional<Document> findById(UUID id) {
        return documentJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Document> findAll() {
        return documentJpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        documentJpaRepository.deleteById(id);
    }

    private DocumentEntity toEntity(Document document) {
        return new DocumentEntity(
                document.getId(),
                document.getFileName(),
                document.getFileType(),
                document.getFilePath(),
                document.getFileSize(),
                document.getStatus(),
                document.getCreatedAt()
        );
    }

    private Document toDomain(DocumentEntity entity) {
        return new Document(
                entity.getId(),
                entity.getFileName(),
                entity.getFileType(),
                entity.getFilePath(),
                entity.getFileSize(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
