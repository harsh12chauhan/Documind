
package com.documind.presentation.controller;

import com.documind.application.port.in.DocumentUseCase;
import com.documind.domain.model.Document;
import com.documind.presentation.dto.response.DocumentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.documind.application.port.in.UploadDocumentUseCase;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpStatus;


import java.io.IOException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentUseCase documentUseCase;
    private final UploadDocumentUseCase uploadDocumentUseCase;

    public DocumentController(DocumentUseCase documentUseCase, UploadDocumentUseCase uploadDocumentUseCase) {
        this.documentUseCase = documentUseCase;
        this.uploadDocumentUseCase = uploadDocumentUseCase;
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {

        List<DocumentResponse> documents = documentUseCase
                .getAllDocuments()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(
            @PathVariable UUID id
    ) {
        Document document = documentUseCase.getDocumentById(id);

        return ResponseEntity.ok(toResponse(document));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable UUID id
    ) {
        documentUseCase.deleteDocument(id);

        return ResponseEntity.noContent().build();
    }

    private DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getFileName(),
                document.getFileType(),
                document.getFileSize(),
                document.getStatus(),
                document.getCreatedAt()
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file) {

        try {
            Document document = uploadDocumentUseCase.uploadDocument(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );

            DocumentResponse response = new DocumentResponse(
                    document.getId(),
                    document.getFileName(),
                    document.getFileType(),
                    document.getFileSize(),
                    document.getStatus(),
                    document.getCreatedAt()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read uploaded file", exception);
        }
    }

}
