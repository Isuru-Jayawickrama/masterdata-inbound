package com.sysco.masterdata_inbound.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.masterdata_inbound.entity.FailedMessage;
import com.sysco.masterdata_inbound.entity.FailedMessageStatus;
import com.sysco.masterdata_inbound.model.NotificationMessage;
import com.sysco.masterdata_inbound.repository.FailedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FailedMessageRetryJob {

    private static final int MAX_RETRIES = 5;

    private final FailedMessageRepository repository;
    private final FailedMessageService failedMessageService;
    private final NotificationProcessingService processingService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 300000)
    public void retryFailedMessages() {

        List<FailedMessage> failedMessages = repository.findByStatusAndNextRetryAtBefore(FailedMessageStatus.FAILED, LocalDateTime.now());

        log.info("Found {} failed messages for retry", failedMessages.size());

        for (FailedMessage failedMessage : failedMessages) {

            retry(failedMessage);
        }
    }

    private void retry(FailedMessage failedMessage) {

        try {

            failedMessage.setStatus(FailedMessageStatus.RETRYING);

            repository.save(failedMessage);

            NotificationMessage message = objectMapper.readValue(failedMessage.getPayload(), NotificationMessage.class);

            processingService.processRetry(message);

            failedMessageService.markSuccess(failedMessage);

            log.info("Retry succeeded for failed message {}", failedMessage.getId());

        } catch (Exception ex) {

            log.error("Retry failed for message {}", failedMessage.getId(), ex);

            if (failedMessage.getRetryCount() >= MAX_RETRIES) {

                failedMessageService.markDead(failedMessage);

            } else {

                failedMessageService.updateRetry(failedMessage);
            }
        }
    }
}