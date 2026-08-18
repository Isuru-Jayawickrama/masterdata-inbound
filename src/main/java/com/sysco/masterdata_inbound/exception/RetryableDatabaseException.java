package com.sysco.masterdata_inbound.exception;

public class RetryableDatabaseException extends ProcessingException {

    public RetryableDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public boolean isRetryable() {
        return true;
    }
}