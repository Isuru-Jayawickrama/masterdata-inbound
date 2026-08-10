package com.sysco.masterdata_inbound.sql;

import com.sysco.masterdata_inbound.config.MasterDataConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UpsertSqlBuilder {

    public SqlStatement build(
            MasterDataConfig config,
            Map<String, Object> data
    ) {

        String columns =
                String.join(
                        ", ",
                        data.keySet()
                );

        String placeholders =
                data.keySet()
                        .stream()
                        .map(c -> "?")
                        .collect(
                                Collectors.joining(", ")
                        );

        String updateClause =
                data.keySet()
                        .stream()
                        .filter(
                                c ->
                                        !c.equals(
                                                config.getPrimaryKey()
                                        )
                        )
                        .map(
                                c ->
                                        c
                                                + " = EXCLUDED."
                                                + c
                        )
                        .collect(
                                Collectors.joining(", ")
                        );

        String sql =
                """
                INSERT INTO %s
                (%s)

                VALUES (%s)

                ON CONFLICT (%s)

                DO UPDATE

                SET %s
                """
                        .formatted(
                                config.getTargetTable(),
                                columns,
                                placeholders,
                                config.getPrimaryKey(),
                                updateClause
                        );

        return new SqlStatement(
                sql,
                new ArrayList<>(
                        data.values()
                )
        );
    }
}