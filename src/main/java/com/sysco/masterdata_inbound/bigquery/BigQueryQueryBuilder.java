package com.sysco.masterdata_inbound.bigquery;

import com.sysco.masterdata_inbound.config.MasterDataConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BigQueryQueryBuilder {

    public String build(MasterDataConfig config) {

        String selectedColumns = String.join(", ", config.getSelectedFields());

        String query = """
                SELECT %s
                FROM `%s`
                WHERE updated_at > TIMESTAMP(@cutoff_start_time)
                  AND updated_at <= TIMESTAMP(@cutoff_end_time)
                  AND subscriber_id IN UNNEST(@subscriber_id_list)
                """.formatted(selectedColumns, config.getSourceView());

        log.debug("Generated BigQuery query for domain {}", config.getDomain());

        return query;
    }
}