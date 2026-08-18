package com.sysco.masterdata_inbound.exception;

public class NonRetryableDatabaseException extends ProcessingException {

    public NonRetryableDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public boolean isRetryable() {
        return false;
    }
}
