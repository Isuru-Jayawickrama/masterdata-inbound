package com.sysco.masterdata_inbound.validation;

import com.sysco.masterdata_inbound.config.MasterDataConfig;
import com.sysco.masterdata_inbound.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigurationValidationService {

    public void validate(MasterDataConfig config) {

        validateNotBlank(config.getDomain(), "domain");
        validateNotBlank(config.getSourceView(), "sourceView");
        validateNotBlank(config.getTargetTable(), "targetTable");
        validateNotBlank(config.getPrimaryKey(), "primaryKey");

        if (config.getSelectedFields() == null || config.getSelectedFields().isEmpty()) {
            throw new ValidationException("selectedFields cannot be empty for domain " + config.getDomain());
        }

        if (config.getFieldMappings() == null || config.getFieldMappings().isEmpty()) {
            throw new ValidationException("fieldMappings cannot be empty for domain " + config.getDomain());
        }

        log.info("Configuration validation successful for domain {}", config.getDomain());
    }

    private void validateNotBlank(String value, String fieldName) {

        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }
}