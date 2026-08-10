package com.sysco.masterdata_inbound.util;

import com.sysco.masterdata_inbound.consumer.PubSubMessageReceiver;
import com.sysco.masterdata_inbound.model.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StartupRunner
        implements CommandLineRunner {

    private final PubSubMessageReceiver receiver;

    @Override
    public void run(String... args) {

        NotificationMessage message =
                new NotificationMessage();

        message.setDomain("item");

        message.setCutoffStartTime(
                "2025-12-11T13:29:01.452944+00:00"
        );

        message.setCutoffEndTime(
                "2025-12-12T00:25:08.536563+00:00"
        );

        message.setSubscriberIds(
                List.of(
                        "152",
                        "072"
                )
        );

        receiver.receive(message);
    }
}