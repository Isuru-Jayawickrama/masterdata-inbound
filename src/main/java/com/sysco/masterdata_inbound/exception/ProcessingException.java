package com.sysco.masterdata_inbound.exception;

public abstract class ProcessingException extends RuntimeException {

    protected ProcessingException(String message) {
        super(message);
    }

    protected ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract boolean isRetryable();
}