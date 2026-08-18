package com.sysco.masterdata_inbound.exception;

public class NonRetryableBigQueryException extends ProcessingException {

    public NonRetryableBigQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public boolean isRetryable() {
        return false;
    }
}