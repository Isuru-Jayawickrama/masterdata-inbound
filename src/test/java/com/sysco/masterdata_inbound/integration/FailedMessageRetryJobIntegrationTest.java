package com.sysco.masterdata_inbound.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.sysco.masterdata_inbound.bigquery.BigQueryService;
import com.sysco.masterdata_inbound.entity.FailedMessage;
import com.sysco.masterdata_inbound.entity.FailedMessageStatus;
import com.sysco.masterdata_inbound.exception.RetryableBigQueryException;
import com.sysco.masterdata_inbound.model.NotificationMessage;
import com.sysco.masterdata_inbound.repository.FailedMessageRepository;
import com.sysco.masterdata_inbound.service.FailedMessageRetryJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class FailedMessageRetryJobIntegrationTest {

    @Autowired
    private FailedMessageRetryJob retryJob;

    @Autowired
    private FailedMessageRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BigQueryService bigQueryService;

    @MockitoBean
    private BigQuery bigQuery;

    @BeforeEach
    void setup() {

        repository.deleteAll();
    }

    @Test
    void should_mark_message_as_success_when_retry_succeeds() throws Exception {

        NotificationMessage notification = new NotificationMessage();

        notification.setDomain("item");

        FailedMessage failed = new FailedMessage();

        failed.setDomain("item");

        failed.setPayload(objectMapper.writeValueAsString(notification));

        failed.setRetryCount(0);

        failed.setStatus(FailedMessageStatus.FAILED);

        failed.setCreatedAt(LocalDateTime.now());

        failed.setNextRetryAt(LocalDateTime.now().minusMinutes(1));

        repository.save(failed);

        Map<String, Object> row = new HashMap<>();

        row.put("supc", "1005");
        row.put("item_name", "Chicken Wings");

        when(bigQueryService.queryData(any(), any())).thenReturn(List.of(row));

        retryJob.retryFailedMessages();

        FailedMessage updated = repository.findById(failed.getId()).orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(FailedMessageStatus.SUCCESS);
    }
}