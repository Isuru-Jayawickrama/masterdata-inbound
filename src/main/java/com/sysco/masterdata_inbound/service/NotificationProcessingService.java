package com.sysco.masterdata_inbound.service;

import com.sysco.masterdata_inbound.bigquery.BigQueryService;
import com.sysco.masterdata_inbound.config.ConfigurationCache;
import com.sysco.masterdata_inbound.config.MasterDataConfig;
import com.sysco.masterdata_inbound.database.DatabaseSyncService;
import com.sysco.masterdata_inbound.mapper.RecordMapper;
import com.sysco.masterdata_inbound.model.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationProcessingService {

    private final ConfigurationCache configurationCache;
    private final BigQueryService bigQueryService;
    private final RecordMapper recordMapper;
    private final DatabaseSyncService databaseSyncService;

    public void process(NotificationMessage message) {

        MasterDataConfig config =
                configurationCache.get(
                        message.getDomain()
                );

        if (config == null) {

            throw new RuntimeException(
                    "No configuration found for domain : "
                            + message.getDomain()
            );
        }

        List<Map<String, Object>> rows =
                bigQueryService.queryData(
                        config,
                        message
                );

        System.out.println(
                "Records returned : "
                        + rows.size()
        );

        for (Map<String,Object> row : rows) {

            Map<String,Object> mapped =
                    recordMapper.map(
                            row,
                            config
                    );

            databaseSyncService.sync(
                    config,
                    mapped
            );
        }

        rows.forEach(System.out::println);
    }
}