package com.poc.alerting.amqp;

import java.io.Serializable;

import org.springframework.amqp.core.Message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDataTransferObject implements Serializable {
    private String routingKey;
    private String exchange;
    private Message message;
}
