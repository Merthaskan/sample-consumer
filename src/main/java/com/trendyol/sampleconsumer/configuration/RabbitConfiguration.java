package com.trendyol.sampleconsumer.configuration;


import com.trendyol.sampleconsumer.properties.RabbitConfigurationProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RabbitConfiguration {
    @Bean
    @Primary
    public ConnectionFactory rabbitConnectionFactory(RabbitProperties rabbitProperties, @Value("${spring.application.name}") String appName) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost(rabbitProperties.getHost());
        cachingConnectionFactory.setPort(rabbitProperties.getPort());
        cachingConnectionFactory.setUsername(rabbitProperties.getUsername());
        cachingConnectionFactory.setPassword(rabbitProperties.getPassword());
        cachingConnectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());
        cachingConnectionFactory.setConnectionNameStrategy(connectionFactory -> appName);
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @Primary
    public RabbitTemplate rabbitTemplate(RabbitAdmin rabbitAdmin, Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = rabbitAdmin.getRabbitTemplate();
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        rabbitTemplate.setChannelTransacted(true);
        return rabbitTemplate;
    }

    @Bean
    public RetryOperationsInterceptor mqRetryInterceptor(MessageRecoverer messageRecoverer, RabbitConfigurationProperties configurationProperties) {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(configurationProperties.getRetryPolicy().getMaxAttempt())
                .backOffOptions(configurationProperties.getRetryPolicy().getInitialInterval(),
                        configurationProperties.getRetryPolicy().getMultiplier(),
                        configurationProperties.getRetryPolicy().getMaxInterval())
                .recoverer(messageRecoverer)
                .build();
    }

    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory rabbitConnectionFactory,
                                                                         Jackson2JsonMessageConverter jackson2JsonMessageConverter,
                                                                         RetryOperationsInterceptor mqRetryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        factory.setAdviceChain(mqRetryInterceptor);
        return factory;
    }

    @Bean
    public Queue createConsumeQueue(RabbitAdmin rabbitAdmin, RabbitConfigurationProperties properties) {
        Queue consumeQueue = QueueBuilder.durable(properties.getEvent().getQueue()).build();
        consumeQueue.setAdminsThatShouldDeclare(rabbitAdmin);
        return consumeQueue;
    }

    @Bean
    public Queue createDelayedMessageQueue(RabbitAdmin rabbitAdmin, RabbitConfigurationProperties properties) {
        Queue delayedMessageQueue = QueueBuilder.durable(properties.getEvent().getDelayQueue())
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", properties.getEvent().getQueue())
                .withArgument("x-queue-mode", "lazy")
                .build();
        delayedMessageQueue.setAdminsThatShouldDeclare(rabbitAdmin);
        return delayedMessageQueue;
    }

    @Bean
    public TopicExchange createConsumeExchange(RabbitAdmin rabbitAdmin,RabbitConfigurationProperties properties){
        TopicExchange exchange = new TopicExchange(properties.getEvent().getExchange());
        exchange.setAdminsThatShouldDeclare(rabbitAdmin);
        return exchange;
    }

    @Bean
    public Binding createQueueExchangeBinding(RabbitConfigurationProperties properties){
        return new Binding(properties.getEvent().getQueue(),Binding.DestinationType.QUEUE,properties.getEvent().getExchange(),"#",null);
    }
}
