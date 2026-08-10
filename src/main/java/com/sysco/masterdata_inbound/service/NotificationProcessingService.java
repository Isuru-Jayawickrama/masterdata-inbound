package com.sysco.masterdata_inbound.service;

import com.sysco.masterdata_inbound.bigquery.BigQueryService;
import com.sysco.masterdata_inbound.config.ConfigurationCache;
import com.sysco.masterdata_inbound.config.MasterDataConfig;
import com.sysco.masterdata_inbound.database.DatabaseSyncService;
import com.sysco.masterdata_inbound.mapper.RecordMapper;
import com.sysco.masterdata_inbound.model.NotificationMessage;
import com.sysco.masterdata_inbound.validation.PayloadValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessingService {

    private final ConfigurationCache configurationCache;
    private final BigQueryService bigQueryService;
    private final RecordMapper recordMapper;
    private final DatabaseSyncService databaseSyncService;
    private final PayloadValidationService payloadValidationService;

    public void process(NotificationMessage message) {
        log.info("Processing notification for domain {}", message.getDomain());

        payloadValidationService.validate(message);

        MasterDataConfig config = configurationCache.get(message.getDomain());

        if (config == null) {

            throw new RuntimeException("No configuration found for domain : " + message.getDomain());
        }

        List<Map<String, Object>> rows = bigQueryService.queryData(config, message);

        System.out.println("Records returned : " + rows.size());

        for (Map<String, Object> row : rows) {

            Map<String, Object> mapped = recordMapper.map(row, config);

            databaseSyncService.sync(config, mapped);
        }

        rows.forEach(System.out::println);
    }
}