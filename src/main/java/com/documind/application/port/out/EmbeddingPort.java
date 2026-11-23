
package com.documind.application.port.out;

import java.util.List;

public interface EmbeddingPort {

    List<Float> generateEmbedding(String text);
}
