
package com.documind.infrastructure.ai.embedding;

import com.documind.application.port.out.ChatCompletionPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class GoogleChatCompletionAdapter implements ChatCompletionPort {

    private final ChatClient chatClient;

    public GoogleChatCompletionAdapter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String generateAnswer(String question, String context) {

        String systemPrompt = """
                You are DocuMind, an AI assistant that answers questions
                using the provided document context.

                Follow these rules:
                1. Answer using only the information in the provided context.
                2. Do not invent facts or use outside knowledge.
                3. If the answer is not available in the context,
                   clearly say that the document does not contain the answer.
                4. Keep your answer clear, accurate, and relevant.
                """;

        String userPrompt = """
                Document context:
                %s

                User question:
                %s
                """.formatted(context, question);

        String answer = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();

        if (answer == null || answer.isBlank()) {
            throw new IllegalStateException(
                    "The AI model returned an empty response"
            );
        }

        return answer;
    }
}
