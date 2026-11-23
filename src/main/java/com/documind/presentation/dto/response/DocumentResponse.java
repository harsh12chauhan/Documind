
package com.documind.presentation.dto.response;

import com.documind.domain.enums.DocumentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(

        UUID id,
        String fileName,
        String fileType,
        long fileSize,
        DocumentStatus status,
        LocalDateTime createdAt

) {
}
