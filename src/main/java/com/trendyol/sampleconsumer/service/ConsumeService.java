package com.trendyol.sampleconsumer.service;

import com.trendyol.sampleconsumer.event.SampleEvent;
import com.trendyol.sampleconsumer.exception.DelayException;
import org.springframework.stereotype.Service;

@Service
public class ConsumeService {
    public void consumeSampleEvent(SampleEvent event) {
        int idLastTwoDigit = event.getMessageId() % 100;
        if (idLastTwoDigit >= 0 && idLastTwoDigit <= 30) {
            throw new DelayException("Test Event");
        }
    }
}
