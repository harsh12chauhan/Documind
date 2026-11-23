
package com.documind.application.service;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class DocumentFileValidator {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final Map<String, String> ALLOWED_FILE_TYPES = Map.of(
            ".pdf", "application/pdf",
            ".txt", "text/plain",
            ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    public void validate(
            String fileName,
            String contentType,
            byte[] content
    ) {

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }

        if (fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (content.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File size cannot exceed 10 MB"
            );
        }

        String extension = getExtension(fileName);

        String expectedContentType = ALLOWED_FILE_TYPES.get(extension);

        if (expectedContentType == null) {
            throw new IllegalArgumentException(
                    "Unsupported file format. Allowed formats: PDF, TXT, DOCX"
            );
        }

        String actualContentType = contentType == null
                ? ""
                : contentType.split(";")[0].trim().toLowerCase(Locale.ROOT);

        if (!expectedContentType.equals(actualContentType)) {
            throw new IllegalArgumentException(
                    "File content type does not match its extension"
            );
        }

        validateFileSignature(extension, content);
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');

        if (index < 0) {
            return "";
        }

        return fileName.substring(index).toLowerCase(Locale.ROOT);
    }

    private void validateFileSignature(
            String extension,
            byte[] content
    ) {

        if (".pdf".equals(extension)) {
            if (content.length < 5
                    || content[0] != '%'
                    || content[1] != 'P'
                    || content[2] != 'D'
                    || content[3] != 'F'
                    || content[4] != '-') {
                throw new IllegalArgumentException(
                        "Invalid PDF file"
                );
            }
        }

        if (".docx".equals(extension)) {
            if (content.length < 4
                    || content[0] != 'P'
                    || content[1] != 'K'
                    || content[2] != 3
                    || content[3] != 4) {
                throw new IllegalArgumentException(
                        "Invalid DOCX file"
                );
            }
        }

        if (".txt".equals(extension)) {
            for (byte value : content) {
                if (value == 0) {
                    throw new IllegalArgumentException(
                            "Invalid text file"
                    );
                }
            }
        }
    }
}
