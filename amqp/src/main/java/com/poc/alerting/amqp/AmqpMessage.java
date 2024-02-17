package com.poc.alerting.amqp;

import java.io.Serializable;

public interface AmqpMessage extends Serializable {
    String correlationId = "correlation-id";

    String getRoutingKey();

    String getExchange();
}
