package com.sysco.masterdata_inbound.bigquery;

import com.sysco.masterdata_inbound.config.MasterDataConfig;
import org.springframework.stereotype.Component;

@Component
public class BigQueryQueryBuilder {

    public String build(
            MasterDataConfig config
    ) {

        return """
                SELECT *
                FROM `%s`
                WHERE updated_at >
                    TIMESTAMP(@cutoff_start_time)

                AND updated_at <=
                    TIMESTAMP(@cutoff_end_time)

                AND subscriber_id IN
                    UNNEST(@subscriber_id_list)
                """
                .formatted(
                        config.getSourceView()
                );
    }
}