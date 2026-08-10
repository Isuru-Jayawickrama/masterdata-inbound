package com.sysco.masterdata_inbound.config;

import lombok.Data;

import java.util.List;

@Data
public class MasterDataConfig {

    private String domain;

    private String sourceView;

    private String targetTable;

    private String primaryKey;

    private List<FieldMapping> fieldMappings;
}