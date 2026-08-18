package com.sysco.masterdata_inbound.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.masterdata_inbound.exception.ConfigurationException;
import com.sysco.masterdata_inbound.validation.ConfigurationValidationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigurationLoader {

    private final ConfigurationCache cache;
    private final ObjectMapper objectMapper;
    private final ConfigurationValidationService validationService;

    @PostConstruct
    public void load() {

        try {

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            Resource[] resources = resolver.getResources("classpath:mappings/*.json");

            for (Resource resource : resources) {

                MasterDataConfig config = objectMapper.readValue(resource.getInputStream(), MasterDataConfig.class);

                validationService.validate(config);

                cache.add(config);

                log.info("Loaded configuration for domain {}", config.getDomain());
            }

            log.info("Successfully loaded {} domain configurations", resources.length);

        } catch (Exception ex) {

            log.error("Failed loading domain configurations", ex);

            throw new ConfigurationException("Failed loading mappings", ex);
        }
    }
}