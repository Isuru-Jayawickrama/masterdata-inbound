package com.sysco.masterdata_inbound.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NotificationMessage {

    private String domain;

    @JsonProperty("cutoff_start_time")
    private String cutoffStartTime;

    @JsonProperty("cutoff_end_time")
    private String cutoffEndTime;

    @JsonProperty("subscriber_id")
    private List<String> subscriberIds;
}