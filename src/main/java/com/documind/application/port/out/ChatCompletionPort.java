
package com.documind.application.port.out;

public interface ChatCompletionPort {

    String generateAnswer(String question, String context);
}
