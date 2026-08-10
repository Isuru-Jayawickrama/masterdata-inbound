package com.sysco.masterdata_inbound.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigurationCache {

    private final Map<String, MasterDataConfig> configs =
            new HashMap<>();

    public void add(MasterDataConfig config) {
        configs.put(
                config.getDomain(),
                config
        );
    }

    public MasterDataConfig get(String domain) {
        return configs.get(domain);
    }

    public boolean contains(String domain) {
        return configs.containsKey(domain);
    }
}