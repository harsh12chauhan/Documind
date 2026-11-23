
package com.documind.presentation.controller;

import com.documind.application.port.in.DocumentUseCase;
import com.documind.application.port.in.ProcessDocumentUseCase;
import com.documind.application.port.in.UploadDocumentUseCase;
import com.documind.domain.model.Document;
import com.documind.domain.model.DocumentChunk;
import com.documind.presentation.dto.response.DocumentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.documind.application.port.in.AskQuestionUseCase;
import com.documind.presentation.dto.request.AskQuestionRequest;
import com.documind.presentation.dto.response.AskQuestionResponse;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentUseCase documentUseCase;
    private final UploadDocumentUseCase uploadDocumentUseCase;
    private final ProcessDocumentUseCase processDocumentUseCase;
    private final AskQuestionUseCase askQuestionUseCase;

    public DocumentController(
            DocumentUseCase documentUseCase,
            UploadDocumentUseCase uploadDocumentUseCase,
            ProcessDocumentUseCase processDocumentUseCase, AskQuestionUseCase askQuestionUseCase) {
        this.documentUseCase = documentUseCase;
        this.uploadDocumentUseCase = uploadDocumentUseCase;
        this.processDocumentUseCase = processDocumentUseCase;
        this.askQuestionUseCase = askQuestionUseCase;
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
            @PathVariable UUID id) {
        Document document = documentUseCase.getDocumentById(id);
        return ResponseEntity.ok(toResponse(document));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentUseCase.deleteDocument(id);
        return ResponseEntity.noContent().build();
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

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(document));

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to read uploaded file", exception
            );
        }
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<List<DocumentChunk>> processDocument(
            @PathVariable UUID id) {
        List<DocumentChunk> chunks =
                processDocumentUseCase.processDocument(id);

        return ResponseEntity.ok(chunks);
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

    @PostMapping("/{id}/ask")
    public ResponseEntity<AskQuestionResponse> askQuestion(
            @PathVariable UUID id,
            @Valid @RequestBody AskQuestionRequest request) {

        String answer = askQuestionUseCase.askQuestion(
                id,
                request.question()
        );

        AskQuestionResponse response = new AskQuestionResponse(
                id.toString(),
                request.question(),
                answer
        );

        return ResponseEntity.ok(response);
    }

}
