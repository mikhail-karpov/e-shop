package com.mikhailkarpov.eshop.orders.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final OrderMessagingProperties messagingProperties;

    @Bean
    public TopicExchange orderTopicExchange() {
        return new TopicExchange(messagingProperties.getTopicExchange());
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(messagingProperties.getCreatedQueue());
    }

    @Bean
    public Queue orderUpdatedQueue() {
        return new Queue(messagingProperties.getUpdatedQueue());
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder
                .bind(orderCreatedQueue())
                .to(orderTopicExchange())
                .with(messagingProperties.getCreatedRoutingKey());
    }

    @Bean
    public Binding orderUpdatedBinding() {
        return BindingBuilder
                .bind(orderUpdatedQueue())
                .to(orderTopicExchange())
                .with(messagingProperties.getUpdatedRoutingKey());
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter(new ObjectMapper());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
