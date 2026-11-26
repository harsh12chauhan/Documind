
package com.documind.application.service;

import com.documind.application.port.in.AskQuestionUseCase;
import com.documind.application.port.out.ChatCompletionPort;
import com.documind.application.port.out.DocumentPersistencePort;
import com.documind.application.port.out.VectorStorePort;
import com.documind.domain.exception.DocumentNotFoundException;
import com.documind.domain.model.Document;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Service
public class AskQuestionService implements AskQuestionUseCase {

    private static final int DEFAULT_TOP_K = 5;

    private static final Logger log =
            LoggerFactory.getLogger(AskQuestionService.class);

    private final DocumentPersistencePort documentPersistencePort;
    private final VectorStorePort vectorStorePort;
    private final ChatCompletionPort chatCompletionPort;

    public AskQuestionService(
            DocumentPersistencePort documentPersistencePort,
            VectorStorePort vectorStorePort,
            ChatCompletionPort chatCompletionPort) {

        this.documentPersistencePort = documentPersistencePort;
        this.vectorStorePort = vectorStorePort;
        this.chatCompletionPort = chatCompletionPort;
    }

    @Override
    public String askQuestion(UUID documentId, String question) {

        long totalStart = System.currentTimeMillis();

        System.out.println("ASK: Started for document " + documentId);

        if (documentId == null) {
            throw new IllegalArgumentException("Document ID is required");
        }

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question cannot be empty");
        }

        Document document = documentPersistencePort.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "Document not found with id: " + documentId
                ));

        System.out.println("ASK: Document found");

        long searchStart = System.currentTimeMillis();

        System.out.println("ASK: Starting vector similarity search");

        List<String> relevantChunks = vectorStorePort.searchSimilar(
                document.getId(),
                question,
                DEFAULT_TOP_K
        );

        System.out.println("ASK: Vector search finished in "
                + (System.currentTimeMillis() - searchStart)
                + " ms. Chunks found: " + relevantChunks.size());

        if (relevantChunks.isEmpty()) {
            return "I couldn't find relevant information in this document.";
        }

        String context = String.join("\n\n", relevantChunks);

        long chatStart = System.currentTimeMillis();

        System.out.println("ASK: Starting Gemini generation");

        String answer = chatCompletionPort.generateAnswer(question, context);

        System.out.println("ASK: Gemini finished in "
                + (System.currentTimeMillis() - chatStart) + " ms");

        System.out.println("ASK: Total time "
                + (System.currentTimeMillis() - totalStart) + " ms");

        return answer;
    }
}
