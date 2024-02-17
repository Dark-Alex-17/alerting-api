package com.poc.alerting.batch;

import org.apache.commons.logging.Log;
import org.springframework.amqp.support.ConditionalExceptionLogger;

public class ExclusiveConsumerExceptionLogger implements ConditionalExceptionLogger {
    @Override
    public void log(final Log logger, final String message, final Throwable t) {
        //do not log exclusive consumer warnings
    }
}
