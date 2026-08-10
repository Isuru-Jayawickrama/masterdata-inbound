package com.sysco.masterdata_inbound.validation;

import com.sysco.masterdata_inbound.exception.ValidationException;
import com.sysco.masterdata_inbound.model.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class PayloadValidationService {

    public void validate(NotificationMessage message) {

        validateRequiredFields(message);

        validateCutoffWindow(message);

        log.debug("Payload validation successful for domain {}", message.getDomain());
    }

    private void validateRequiredFields(NotificationMessage message) {

        if (message.getDomain() == null || message.getDomain().isBlank()) {

            throw new ValidationException("Domain is required");
        }

        if (message.getCutoffStartTime() == null || message.getCutoffStartTime().isBlank()) {

            throw new ValidationException("cutoffStartTime is required");
        }

        if (message.getCutoffEndTime() == null || message.getCutoffEndTime().isBlank()) {

            throw new ValidationException("cutoffEndTime is required");
        }

        if (message.getSubscriberIds() == null || message.getSubscriberIds().isEmpty()) {

            throw new ValidationException("subscriberIds is required");
        }
    }

    private void validateCutoffWindow(NotificationMessage message) {

        try {

            Instant start = Instant.parse(message.getCutoffStartTime());

            Instant end = Instant.parse(message.getCutoffEndTime());

            if (!start.isBefore(end)) {

                throw new ValidationException("cutoffStartTime must be before cutoffEndTime");
            }

        } catch (Exception ex) {

            throw new ValidationException("Invalid timestamp format");
        }
    }
}