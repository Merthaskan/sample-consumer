package com.trendyol.sampleconsumer.recover;

import com.trendyol.sampleconsumer.recover.strategy.RabbitStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomMessageRecover implements MessageRecoverer {
    private final List<RabbitStrategy> rabbitStrategies;

    @Override
    public void recover(Message message, Throwable throwable) {
        for (RabbitStrategy rabbitStrategy: rabbitStrategies) {
            boolean isSuccess = rabbitStrategy.process(message,throwable);
            if (isSuccess){
                rabbitStrategy.recover(message,throwable);
                break;
            }
        }
    }
}
