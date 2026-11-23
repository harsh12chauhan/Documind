
package com.documind.application.port.in;

import java.util.UUID;

public interface AskQuestionUseCase {

    String askQuestion(UUID documentId, String question);
}
