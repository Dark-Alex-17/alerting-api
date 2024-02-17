package com.poc.alerting.batch;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.poc.alerting.amqp.AmqpMessage;
import com.poc.alerting.amqp.AmqpResponse;
import com.poc.alerting.amqp.ExceptionType;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.poc.alerting.amqp.AmqpConfiguration.AMQP_NAME;
import static com.poc.alerting.batch.WorkerConfiguration.NEW_CONSUMER;

@Slf4j
@Component
@AllArgsConstructor
public class QueueCreator {
    private final RabbitAdmin rabbitAdmin;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange directExchange;

    public <T> AmqpResponse<T> createQueue(final AmqpMessage amqpMessage) throws IOException, ClassNotFoundException {
        final String routingKey = amqpMessage.getRoutingKey();
        LOG.info("Attempting to create new queue {}", routingKey);
        setupQueueForAccount(routingKey);
        final AmqpResponse response = (AmqpResponse) rabbitTemplate.convertSendAndReceive(AMQP_NAME, routingKey, amqpMessage);
        if (response != null && ExceptionType.INVALID_ACCOUNT_EXCEPTION.equals(response.getExceptionType())) {
            LOG.info("Invalid account, removing queue {}", routingKey);
            CompletableFuture.runAsync(() -> rabbitAdmin.deleteQueue(routingKey, false, true));
        }
        return response;
    }

    private void setupQueueForAccount(final String routingKey) {
        final Properties properties = rabbitAdmin.getQueueProperties(routingKey);
        if (properties == null) {
            final Queue queue = QueueBuilder.nonDurable(routingKey)
                                            .withArgument("x-expires", DateUtils.MILLIS_PER_DAY)
                                            .build();

            rabbitAdmin.declareQueue(queue);
            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(directExchange).with(routingKey));
            sendCreateConsumerMessage(routingKey);
        } else if ((Integer) properties.get(RabbitAdmin.QUEUE_CONSUMER_COUNT) < 1) {
            LOG.info("{} queue already exists. Adding consumer.", routingKey);
            sendCreateConsumerMessage(routingKey);
        }
    }

    private void sendCreateConsumerMessage(final String queueName) {
        rabbitTemplate.convertAndSend(NEW_CONSUMER, "", queueName);
    }
}
