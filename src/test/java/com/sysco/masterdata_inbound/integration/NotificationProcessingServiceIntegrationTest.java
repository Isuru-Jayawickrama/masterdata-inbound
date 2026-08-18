package com.sysco.masterdata_inbound.integration;

import com.google.cloud.bigquery.BigQuery;
import com.sysco.masterdata_inbound.bigquery.BigQueryService;
import com.sysco.masterdata_inbound.entity.FailedMessage;
import com.sysco.masterdata_inbound.entity.FailedMessageStatus;
import com.sysco.masterdata_inbound.exception.RetryableBigQueryException;
import com.sysco.masterdata_inbound.model.NotificationMessage;
import com.sysco.masterdata_inbound.repository.FailedMessageRepository;
import com.sysco.masterdata_inbound.service.NotificationProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class NotificationProcessingServiceIntegrationTest {

    @Autowired
    private NotificationProcessingService notificationProcessingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FailedMessageRepository failedMessageRepository;

    @MockitoBean
    private BigQueryService bigQueryService;

    @MockitoBean
    private BigQuery bigQuery;

    @BeforeEach
    void setup() {

        jdbcTemplate.execute("""
                DELETE FROM item
                """);
    }

    @Test
    void should_process_notification_and_upsert_record() {

        Map<String, Object> bigQueryRow = new HashMap<>();

        bigQueryRow.put("supc", "1005");
        bigQueryRow.put("item_name", "Chicken Wings");

        when(bigQueryService.queryData(any(), any())).thenReturn(List.of(bigQueryRow));

        NotificationMessage message = new NotificationMessage();

        message.setDomain("item");
        message.setCutoffStartTime(Instant.now().minusSeconds(3600).toString());
        message.setCutoffEndTime(Instant.now().toString());
        message.setSubscriberIds(List.of("152"));

        notificationProcessingService.process(message);

        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM item
                WHERE supc = '1005'
                """, Integer.class);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void should_save_failed_message_when_retryable_exception_occurs() {

        when(bigQueryService.queryData(any(), any())).thenThrow(new RetryableBigQueryException("Temporary BigQuery failure", null));

        NotificationMessage message = new NotificationMessage();

        message.setDomain("item");

        message.setCutoffStartTime(Instant.now().minusSeconds(3600).toString());

        message.setCutoffEndTime(Instant.now().toString());

        message.setSubscriberIds(List.of("152"));

        notificationProcessingService.process(message);

        List<FailedMessage> failures = failedMessageRepository.findAll();

        System.out.println("Failures: " + failures);

    }
}