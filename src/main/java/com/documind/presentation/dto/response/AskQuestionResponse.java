
package com.documind.presentation.dto.response;

public record AskQuestionResponse(
        String documentId,
        String question,
        String answer
) {
}
