package com.sysco.masterdata_inbound.config;

import lombok.Data;

@Data
public class FieldMapping {

    private String source;

    private String target;
}