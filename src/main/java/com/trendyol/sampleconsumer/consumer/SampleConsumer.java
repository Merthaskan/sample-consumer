package com.trendyol.sampleconsumer.consumer;

import com.trendyol.sampleconsumer.event.SampleEvent;
import com.trendyol.sampleconsumer.service.ConsumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SampleConsumer {
    private final ConsumeService service;

    @RabbitListener(queues = "${rabbit.event.queue}")
    public void consumeCargoCreateCompletedEvent(SampleEvent event) {
        service.consumeSampleEvent(event);
    }
}
