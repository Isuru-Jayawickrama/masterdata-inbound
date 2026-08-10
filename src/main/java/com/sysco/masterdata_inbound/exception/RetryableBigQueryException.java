package com.sysco.masterdata_inbound.exception;

public class RetryableBigQueryException extends ProcessingException {

    public RetryableBigQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}