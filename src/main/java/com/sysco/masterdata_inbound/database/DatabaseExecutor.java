package com.sysco.masterdata_inbound.database;

import com.sysco.masterdata_inbound.sql.SqlStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseExecutor {

    private final JdbcTemplate jdbcTemplate;

    public void execute(SqlStatement statement) {

        jdbcTemplate.update(statement.sql(), statement.params().toArray());
    }
}