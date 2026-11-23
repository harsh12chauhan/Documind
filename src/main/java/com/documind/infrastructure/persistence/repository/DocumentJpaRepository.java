
package com.documind.infrastructure.persistence.repository;

import com.documind.infrastructure.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentJpaRepository
        extends JpaRepository<DocumentEntity, UUID> {
}
