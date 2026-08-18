//package com.sysco.masterdata_inbound.bigquery;
//
//import com.sysco.masterdata_inbound.config.MasterDataConfig;
//import com.sysco.masterdata_inbound.model.NotificationMessage;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class BigQueryService {
//
//    public List<Map<String, Object>> queryData(
//            MasterDataConfig config,
//            NotificationMessage message
//    ) {
//
//        System.out.println(
//                "Executing query against view : "
//                        + config.getSourceView()
//        );
//
//        System.out.println(
//                "Time Window : "
//                        + message.getCutoffStartTime()
//                        + " -> "
//                        + message.getCutoffEndTime()
//        );
//
//        List<Map<String, Object>> rows =
//                new ArrayList<>();
//
//        Map<String, Object> sample =
//                new HashMap<>();
//
//        sample.put(
//                "supc",
//                "1005"
//        );
//
//        sample.put(
//                "item_name",
//                "Chicken Wings"
//        );
//
//        rows.add(sample);
//
//        return rows;
//    }
//}

package com.sysco.masterdata_inbound.bigquery;

import com.google.cloud.bigquery.*;
import com.sysco.masterdata_inbound.config.MasterDataConfig;
import com.sysco.masterdata_inbound.exception.NonRetryableBigQueryException;
import com.sysco.masterdata_inbound.model.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BigQueryService {

    private final BigQuery bigQuery;

    private final BigQueryQueryBuilder queryBuilder;

    public List<Map<String, Object>> queryData(MasterDataConfig config, NotificationMessage message) {

        String sql = queryBuilder.build(config);

        QueryJobConfiguration queryConfig = buildQueryConfiguration(sql, message);

        try {
            TableResult result = bigQuery.query(queryConfig);

            return mapRows(result);

        } catch (Exception ex) {
            log.error("BigQuery query execution failed", ex);
            throw new NonRetryableBigQueryException("Failed executing BigQuery query", ex);
        }
    }

    private QueryJobConfiguration buildQueryConfiguration(String sql, NotificationMessage message) {

        return QueryJobConfiguration.newBuilder(sql)

                .addNamedParameter("cutoff_start_time", QueryParameterValue.timestamp(message.getCutoffStartTime()))

                .addNamedParameter("cutoff_end_time", QueryParameterValue.timestamp(message.getCutoffEndTime()))

                .addNamedParameter("subscriber_id_list", QueryParameterValue.array(message.getSubscriberIds().toArray(new String[0]), String.class))

                .build();
    }

    private List<Map<String, Object>> mapRows(TableResult result) {

        List<Map<String, Object>> rows = new ArrayList<>();

        for (FieldValueList row : result.iterateAll()) {

            Map<String, Object> mappedRow = new LinkedHashMap<>();

            for (Field field : result.getSchema().getFields()) {

                mappedRow.put(field.getName(), row.get(field.getName()).getValue());
            }

            rows.add(mappedRow);
        }

        return rows;
    }
}