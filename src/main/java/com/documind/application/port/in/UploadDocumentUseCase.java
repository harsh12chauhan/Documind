
package com.documind.application.port.in;

import com.documind.domain.model.Document;

public interface UploadDocumentUseCase {

    Document uploadDocument(
            String fileName,
            String contentType,
            byte[] content
    );
}
