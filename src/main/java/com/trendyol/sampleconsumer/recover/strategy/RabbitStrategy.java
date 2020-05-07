package com.trendyol.sampleconsumer.recover.strategy;

import org.springframework.amqp.core.Message;

public interface RabbitStrategy {
    boolean process(Message message, Throwable cause);
    void recover(Message message,Throwable cause);
}
