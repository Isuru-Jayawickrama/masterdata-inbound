package com.sysco.masterdata_inbound.bigquery;

import com.sysco.masterdata_inbound.config.MasterDataConfig;
import com.sysco.masterdata_inbound.model.NotificationMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BigQueryService {

    public List<Map<String, Object>> queryData(
            MasterDataConfig config,
            NotificationMessage message
    ) {

        System.out.println(
                "Executing query against view : "
                        + config.getSourceView()
        );

        System.out.println(
                "Time Window : "
                        + message.getCutoffStartTime()
                        + " -> "
                        + message.getCutoffEndTime()
        );

        List<Map<String, Object>> rows =
                new ArrayList<>();

        Map<String, Object> sample =
                new HashMap<>();

        sample.put(
                "supc",
                "1005"
        );

        sample.put(
                "item_name",
                "Chicken Wings"
        );

        rows.add(sample);

        return rows;
    }
}