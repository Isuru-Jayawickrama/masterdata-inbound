package com.sysco.masterdata_inbound.exception;

public class ValidationException extends ProcessingException {

    public ValidationException(String message) {
        super(message);
    }

    @Override
    public boolean isRetryable() {
        return false;
    }
}