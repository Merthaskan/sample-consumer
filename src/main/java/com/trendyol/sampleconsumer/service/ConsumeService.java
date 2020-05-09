package com.trendyol.sampleconsumer.service;

import com.trendyol.sampleconsumer.event.SampleEvent;
import com.trendyol.sampleconsumer.exception.DelayException;
import org.springframework.stereotype.Service;

@Service
public class ConsumeService {
    public void consumeSampleEvent(SampleEvent event) {
        //For Demo checks message id and delay that message
        if (event.getMessageId() >= 0 && event.getMessageId() <= 100) {
            throw new DelayException("Test Event");
        }
        System.out.println("Message: " + event.getMessage());
    }
}
