package com.sysco.masterdata_inbound.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.masterdata_inbound.entity.FailedMessage;
import com.sysco.masterdata_inbound.entity.FailedMessageStatus;
import com.sysco.masterdata_inbound.exception.ProcessingException;
import com.sysco.masterdata_inbound.model.NotificationMessage;
import com.sysco.masterdata_inbound.repository.FailedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailedMessageService {

    private final FailedMessageRepository repository;
    private final ObjectMapper objectMapper;

    public void saveFailure(NotificationMessage message, Exception ex) {

        try {

            FailedMessage failed = new FailedMessage();

            failed.setDomain(message.getDomain());

            failed.setPayload(objectMapper.writeValueAsString(message));

            failed.setErrorType(ex.getClass().getSimpleName());

            failed.setErrorMessage(ex.getMessage());

            failed.setRetryCount(0);

            if (ex instanceof ProcessingException processingException) {

                failed.setStatus(processingException.isRetryable() ? FailedMessageStatus.FAILED : FailedMessageStatus.DEAD);

            } else {

                failed.setStatus(FailedMessageStatus.FAILED);
            }

            failed.setCreatedAt(LocalDateTime.now());

            failed.setNextRetryAt(LocalDateTime.now().plusMinutes(5));

            repository.save(failed);

        } catch (Exception e) {
            log.info("Error saving failed. Error {}", e.getMessage());
        }
    }

    public void markSuccess(FailedMessage failed) {

        failed.setStatus(FailedMessageStatus.SUCCESS);

        failed.setProcessedAt(LocalDateTime.now());

        repository.save(failed);
    }

    public void markDead(FailedMessage failed) {

        failed.setStatus(FailedMessageStatus.DEAD);

        repository.save(failed);
    }

    public void updateRetry(FailedMessage failed) {

        int retryCount = failed.getRetryCount() + 1;

        failed.setRetryCount(retryCount);

        failed.setStatus(FailedMessageStatus.FAILED);

        failed.setNextRetryAt(calculateNextRetry(retryCount));

        repository.save(failed);
    }

    private LocalDateTime calculateNextRetry(int retryCount) {

        return switch (retryCount) {

            case 1 -> LocalDateTime.now().plusMinutes(5);

            case 2 -> LocalDateTime.now().plusMinutes(15);

            case 3 -> LocalDateTime.now().plusMinutes(30);

            case 4 -> LocalDateTime.now().plusHours(1);

            default -> LocalDateTime.now().plusHours(2);
        };
    }
}