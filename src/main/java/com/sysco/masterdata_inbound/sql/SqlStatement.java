package com.sysco.masterdata_inbound.sql;

import java.util.List;

public record SqlStatement(
        String sql,
        List<Object> params
) {
}