
package com.documind.infrastructure.document;

import com.documind.application.port.out.DocumentTextExtractionPort;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TikaDocumentTextExtractionAdapter
        implements DocumentTextExtractionPort {

    @Override
    public String extractText(byte[] content, String fileName) {

        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Document content cannot be empty");
        }

        ByteArrayResource resource = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        try {
            TikaDocumentReader reader = new TikaDocumentReader(resource);

            List<Document> documents = reader.get();

            String extractedText = documents.stream()
                    .map(Document::getText)
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(text -> !text.isEmpty())
                    .collect(Collectors.joining("\n\n"));

            if (extractedText.isBlank()) {
                throw new IllegalArgumentException(
                        "No readable text found in document: " + fileName
                );
            }

            return extractedText;

        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Unable to extract text from document: " + fileName,
                    exception
            );
        }
    }
}
