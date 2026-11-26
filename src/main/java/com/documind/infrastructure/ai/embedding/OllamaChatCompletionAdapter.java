
package com.documind.infrastructure.ai.embedding;

import com.documind.application.port.out.ChatCompletionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class OllamaChatCompletionAdapter implements ChatCompletionPort {

    private static final Logger log =
            LoggerFactory.getLogger(OllamaChatCompletionAdapter.class);

    private final RestClient restClient;
    private final String model;

    public OllamaChatCompletionAdapter(
            @Value("${ollama.base-url}") String baseUrl,
            @Value("${ollama.chat-model}") String model) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        this.model = model;
    }

    @Override
    public String generateAnswer(String question, String context) {

        long startTime = System.currentTimeMillis();

        log.info("OLLAMA: Starting answer generation");
        log.info("OLLAMA: Using model: {}", model);

        String systemPrompt = """
                You are DocuMind, an AI assistant that answers questions
                using the provided document context.

                Follow these rules strictly:
                1. Answer using only the information in the provided context.
                2. Do not invent facts or use outside knowledge.
                3. If the answer is not available in the context,
                   clearly state that the document does not contain the answer.
                4. Keep your answer clear, accurate, and relevant.
                """;

        String userPrompt = """
                Document context:
                %s

                User question:
                %s
                """.formatted(context, question);

        try {

            OllamaRequest request = new OllamaRequest(
                    model,
                    List.of(
                            new Message("system", systemPrompt),
                            new Message("user", userPrompt)
                    ),
                    false
            );

            log.info("OLLAMA: Sending request to http://localhost:11434/api/chat");

            OllamaResponse response = restClient.post()
                    .uri("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(OllamaResponse.class);

            if (response == null
                    || response.message() == null
                    || response.message().content() == null
                    || response.message().content().isBlank()) {

                throw new IllegalStateException(
                        "Ollama returned an empty response"
                );
            }

            String answer = response.message().content();

            long elapsed = System.currentTimeMillis() - startTime;

            log.info(
                    "OLLAMA: Answer generated successfully in {} ms",
                    elapsed
            );

            return answer;

        } catch (Exception e) {

            long elapsed = System.currentTimeMillis() - startTime;

            log.error(
                    "OLLAMA: Answer generation failed after {} ms: {}",
                    elapsed,
                    e.getMessage(),
                    e
            );

            throw new RuntimeException(
                    "Failed to generate answer using Ollama",
                    e
            );
        }
    }

    // Request DTOs

    private record OllamaRequest(
            String model,
            List<Message> messages,
            boolean stream
    ) {}

    private record Message(
            String role,
            String content
    ) {}

    // Response DTOs

    private record OllamaResponse(
            String model,
            Message message,
            boolean done
    ) {}
}
