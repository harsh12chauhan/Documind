
package com.documind.application.port.out;

public interface DocumentStoragePort {

    String store(
            String fileName,
            String contentType,
            byte[] content
    );

    void delete(String filePath);
}
