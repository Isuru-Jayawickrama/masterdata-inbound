package com.sysco.masterdata_inbound.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "failed_message")
public class FailedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String domain;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String errorType;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private Integer retryCount;

    @Enumerated(EnumType.STRING)
    private FailedMessageStatus status;

    private LocalDateTime nextRetryAt;

    private LocalDateTime createdAt;

    private LocalDateTime processedAt;
}