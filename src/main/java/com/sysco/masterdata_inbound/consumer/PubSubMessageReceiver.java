package com.sysco.masterdata_inbound.consumer;

import com.sysco.masterdata_inbound.model.NotificationMessage;
import com.sysco.masterdata_inbound.service.NotificationProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PubSubMessageReceiver {

    private final NotificationProcessingService service;

    public void receive(
            NotificationMessage message
    ) {

        service.process(message);
    }
}