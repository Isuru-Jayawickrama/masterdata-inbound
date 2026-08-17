package com.sysco.masterdata_inbound.repository;


import com.sysco.masterdata_inbound.entity.FailedMessage;
import com.sysco.masterdata_inbound.entity.FailedMessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FailedMessageRepository extends JpaRepository<FailedMessage, Long> {

    List<FailedMessage> findByStatusAndNextRetryAtBefore(FailedMessageStatus status, LocalDateTime time);
}