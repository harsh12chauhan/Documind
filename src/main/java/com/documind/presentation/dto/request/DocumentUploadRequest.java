
package com.documind.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record DocumentUploadRequest(

        @NotNull(message = "Document file is required")
        MultipartFile file

) {
}
