package com.trendyol.sampleconsumer.recover.strategy.impl;

import com.trendyol.sampleconsumer.exception.DelayException;
import com.trendyol.sampleconsumer.properties.RabbitConfigurationProperties;
import com.trendyol.sampleconsumer.recover.strategy.RabbitStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class DelayedMessageRabbitStrategy implements RabbitStrategy {
    private final RabbitConfigurationProperties configurationProperties;
    private final AmqpTemplate errorTemplate;

    @Override
    public boolean process(Message message, Throwable cause) {
        return cause.getCause() instanceof DelayException;
    }

    @Override
    public void recover(Message message, Throwable cause) {
        addCustomHeader(message, cause);
        publish(message);
    }

    private void addCustomHeader(Message message, Throwable cause) {
        String errorResponseModel = cause.getCause().getMessage();
        //Custom Header For Show Exception Message
        message.getMessageProperties().getHeaders().put("x-delay-message", errorResponseModel);
        message.getMessageProperties().getHeaders().put("x-delay-message-publish-date", new Date());
    }

    private void publish(Message message) {
        //Delay Queue Routing Key
        String delayRoutingKey = message.getMessageProperties().getConsumerQueue() + configurationProperties.getRecover().getDelayedMessageQueuePrefix();
        //Set TTL to Message
        message.getMessageProperties().setExpiration(String.valueOf(configurationProperties.getRecover().getDelayMillisecond()));
        this.errorTemplate.send(delayRoutingKey, message);
    }
}
