
package com.documind.infrastructure.ai.embedding;

import com.documind.application.port.out.EmbeddingPort;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoogleEmbeddingAdapter implements EmbeddingPort {

    private final EmbeddingModel embeddingModel;

    public GoogleEmbeddingAdapter(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public List<Float> generateEmbedding(String text) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Text cannot be empty when generating embeddings"
            );
        }

        float[] embedding = embeddingModel.embed(text);

        java.util.ArrayList<Float> result =
                new java.util.ArrayList<>(embedding.length);

        for (float value : embedding) {
            result.add(value);
        }

        return List.copyOf(result);
    }
}
