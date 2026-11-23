
package com.documind.infrastructure.persistence.repository;

import com.documind.infrastructure.persistence.entity.DocumentChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentChunkJpaRepository
        extends JpaRepository<DocumentChunkEntity, UUID> {

    List<DocumentChunkEntity> findByDocumentIdOrderByChunkIndexAsc(
            UUID documentId
    );

    void deleteByDocumentId(UUID documentId);
}
