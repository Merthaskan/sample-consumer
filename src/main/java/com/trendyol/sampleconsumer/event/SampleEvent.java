package com.trendyol.sampleconsumer.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SampleEvent {
    private int messageId;
    private String message;
}
