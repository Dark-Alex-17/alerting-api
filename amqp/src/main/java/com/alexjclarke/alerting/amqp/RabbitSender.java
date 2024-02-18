package com.alexjclarke.alerting.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitSender {
    private final RabbitTemplate rabbitTemplate;

    public void send(final AmqpMessage amqpMessage) {
        try {
            LOG.info("Sending message: {}", amqpMessage.toString());
            rabbitTemplate.convertAndSend(amqpMessage.getExchange(), amqpMessage.getRoutingKey(), amqpMessage);
        } catch (final Exception e) {
            LOG.error("Error sending message, serializing to disk", e);
        }
    }

    public <T> T sendAndReceive(final AmqpMessage amqpMessage) {
        final AmqpResponse<T> amqpResponse = (AmqpResponse) rabbitTemplate.convertSendAndReceive(amqpMessage.getExchange(), amqpMessage.getRoutingKey(), amqpMessage);
        final String errorMessage = "Something went wrong";
        if (amqpResponse == null) {
            LOG.error(errorMessage);
        }
        final ExceptionType exceptionType = amqpResponse.getExceptionType();
        if (exceptionType != null) {
            final String exceptionMessage = amqpResponse.getExceptionMessage();
            final int statusCode = exceptionType.getStatus();
            if (amqpResponse.getErrorCode() != null) {
                final Integer errorCode = amqpResponse.getErrorCode();
                final String errorDescription = amqpResponse.getErrorDescription();
                LOG.error(errorMessage);
            } else {
                LOG.error(errorMessage);
            }
        }
        return amqpResponse.getValue();
    }

    public void sendWithoutBackup(final AmqpMessage amqpMessage) {
        LOG.info("Sending message: {}", amqpMessage.toString());
        rabbitTemplate.convertAndSend(amqpMessage.getExchange(), amqpMessage.getRoutingKey(), amqpMessage);
    }
}
