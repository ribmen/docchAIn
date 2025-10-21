package com.docchain.user.domain.exception;

public class DocumentServiceIntegrationException extends RuntimeException {
    public DocumentServiceIntegrationException(String message) {
        super(message);
    }

    public DocumentServiceIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
