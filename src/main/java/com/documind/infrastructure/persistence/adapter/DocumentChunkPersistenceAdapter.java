
package com.documind.infrastructure.persistence.adapter;

import com.documind.application.port.out.DocumentChunkPersistencePort;
import com.documind.domain.model.DocumentChunk;
import com.documind.infrastructure.persistence.entity.DocumentChunkEntity;
import com.documind.infrastructure.persistence.repository.DocumentChunkJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class DocumentChunkPersistenceAdapter
        implements DocumentChunkPersistencePort {

    private final DocumentChunkJpaRepository repository;

    public DocumentChunkPersistenceAdapter(
            DocumentChunkJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void saveAll(List<DocumentChunk> chunks) {
        List<DocumentChunkEntity> entities = chunks.stream()
                .map(chunk -> new DocumentChunkEntity(
                        chunk.documentId(),
                        chunk.chunkIndex(),
                        chunk.content()
                ))
                .toList();

        repository.saveAll(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentChunk> findByDocumentId(UUID documentId) {
        return repository
                .findByDocumentIdOrderByChunkIndexAsc(documentId)
                .stream()
                .map(entity -> new DocumentChunk(
                        entity.getDocumentId(),
                        entity.getChunkIndex(),
                        entity.getContent()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void deleteByDocumentId(UUID documentId) {
        repository.deleteByDocumentId(documentId);
    }
}
