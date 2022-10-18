package com.mrkresnofatihdev.gulugulu.configurations;

import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    Queue userSignupHandlerQueue() {
        return new Queue(Constants.Rabbit.QueueNames.UserSignupQueue, false, false, false);
    }

    @Bean
    Queue sendFriendRequestHandlerQueue() {
        return new Queue(Constants.Rabbit.QueueNames.SendFriendRequestQueue, false, false, false);
    }

    @Bean
    Queue acknowledgeNotificationsHandlerQueue() {
        return new Queue(Constants.Rabbit.QueueNames.AcknowledgeNotificationsQueue, false, false, false);
    }

    @Bean
    DirectExchange guluguluExchange() {
        return new DirectExchange(Constants.Rabbit.ExchangeName);
    }

    @Bean
    Binding userSignupHandlerBinding(Queue userSignupHandlerQueue, DirectExchange guluguluExchange) {
        return BindingBuilder.bind(userSignupHandlerQueue)
                .to(guluguluExchange)
                .with(Constants.Rabbit.RoutingKeys.UserSignupRoute);
    }

    @Bean
    Binding sendFriendRequestHandlerBinding(Queue sendFriendRequestHandlerQueue, DirectExchange guluguluExchange) {
        return BindingBuilder.bind(sendFriendRequestHandlerQueue)
                .to(guluguluExchange)
                .with(Constants.Rabbit.RoutingKeys.SendFriendRequestRoute);
    }

    @Bean
    Binding acknowledgeNotificationsHandlerBinding(Queue acknowledgeNotificationsHandlerQueue, DirectExchange guluguluExchange) {
        return BindingBuilder.bind(acknowledgeNotificationsHandlerQueue)
                .to(guluguluExchange)
                .with(Constants.Rabbit.RoutingKeys.AcknowledgeNotificationsRoute);
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        var rabbitTemplate = new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
