
package com.documind.application.port.out;

public interface DocumentTextExtractionPort {

    String extractText(byte[] content, String fileName);
}
