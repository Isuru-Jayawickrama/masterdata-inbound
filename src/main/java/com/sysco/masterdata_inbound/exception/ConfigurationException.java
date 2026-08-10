package com.sysco.masterdata_inbound.exception;

public class ConfigurationException extends ProcessingException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}