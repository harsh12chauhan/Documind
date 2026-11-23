
package com.documind.application.port.out;

public interface DocumentStoragePort {

    String store(
            String fileName,
            String contentType,
            byte[] content
    );

    byte[] read(String filePath);

    void delete(String filePath);
}

