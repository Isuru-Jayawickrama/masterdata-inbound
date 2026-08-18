package com.sysco.masterdata_inbound.database;

import com.sysco.masterdata_inbound.config.MasterDataConfig;
import com.sysco.masterdata_inbound.sql.SqlStatement;
import com.sysco.masterdata_inbound.sql.UpsertSqlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseSyncService {

    private final UpsertSqlBuilder sqlBuilder;

    private final DatabaseExecutor executor;

    public void sync(MasterDataConfig config, Map<String, Object> row) {

        SqlStatement statement = sqlBuilder.build(config, row);

        executor.execute(statement);

        log.debug("Record synchronized to table {}", config.getTargetTable());
    }
}