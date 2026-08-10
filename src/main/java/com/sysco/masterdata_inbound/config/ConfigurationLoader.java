package com.sysco.masterdata_inbound.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfigurationLoader {

    private final ConfigurationCache cache;

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void load() {

        try {

            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();

            Resource[] resources =
                    resolver.getResources(
                            "classpath:mappings/*.json"
                    );

            for (Resource resource : resources) {

                MasterDataConfig config =
                        objectMapper.readValue(
                                resource.getInputStream(),
                                MasterDataConfig.class
                        );

                validate(config);

                cache.add(config);

                System.out.println(
                        "Loaded domain : "
                                + config.getDomain()
                );
            }

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed loading mappings",
                    ex
            );
        }
    }

    private void validate(
            MasterDataConfig config
    ) {

        if (config.getDomain() == null
                || config.getDomain().isBlank()) {

            throw new RuntimeException(
                    "Domain missing"
            );
        }

        if (config.getSourceView() == null
                || config.getSourceView().isBlank()) {

            throw new RuntimeException(
                    "Source view missing"
            );
        }

        if (config.getTargetTable() == null
                || config.getTargetTable().isBlank()) {

            throw new RuntimeException(
                    "Target table missing"
            );
        }
    }
}