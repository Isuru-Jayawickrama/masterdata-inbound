package com.sysco.masterdata_inbound.sql;

import com.sysco.masterdata_inbound.config.MasterDataConfig;
import com.sysco.masterdata_inbound.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UpsertSqlBuilder {

    public SqlStatement build(MasterDataConfig config, Map<String, Object> data) {
        if (!data.containsKey(config.getPrimaryKey())) {

            throw new ValidationException("Primary key not found in mapped record: " + config.getPrimaryKey());
        }

        String columns = String.join(", ", data.keySet());

        String placeholders = data.keySet().stream().map(c -> "?").collect(Collectors.joining(", "));

        String updateClause = data.keySet().stream().filter(c -> !c.equals(config.getPrimaryKey())).map(c -> c + " = EXCLUDED." + c).collect(Collectors.joining(", "));

        String sql = """
                INSERT INTO %s
                (%s)
                
                VALUES (%s)
                
                ON CONFLICT (%s)
                
                DO UPDATE
                
                SET %s
                """.formatted(config.getTargetTable(), columns, placeholders, config.getPrimaryKey(), updateClause);

        log.debug("Generated UPSERT SQL for table {}", config.getTargetTable());

        return new SqlStatement(sql, new ArrayList<>(data.values()));
    }
}