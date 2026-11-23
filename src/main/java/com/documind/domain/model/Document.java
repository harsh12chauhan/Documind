
package com.documind.domain.model;

import com.documind.domain.enums.DocumentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Document {

    private UUID id;
    private String fileName;
    private String fileType;
    private String filePath;
    private long fileSize;
    private DocumentStatus status;
    private LocalDateTime createdAt;

    public Document(
            UUID id,
            String fileName,
            String fileType,
            String filePath,
            long fileSize,
            DocumentStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void updateStatus(DocumentStatus status) {
        this.status = status;
    }
}
