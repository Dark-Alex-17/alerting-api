package com.alexjclarke.alerting.amqp;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AmqpResponse<T> implements Serializable {
    private T value;
    private ExceptionType exceptionType;
    private String exceptionMessage;
    private Integer errorCode;
    private String errorDescription;
}
