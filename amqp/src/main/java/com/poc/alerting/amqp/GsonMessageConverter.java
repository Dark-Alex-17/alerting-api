package com.poc.alerting.amqp;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GsonMessageConverter implements MessageConverter {
    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

    @Override
    public Message toMessage(final Object object, final MessageProperties messageProperties) throws MessageConversionException {
        if (!(object instanceof Serializable)) {
            throw new MessageConversionException("Message object is not serializable");
        }

        try {
            final String json = GSON.toJson(object);
            if (json == null) {
                throw new MessageConversionException("Unable to serialize the message to JSON");
            }

            LOG.debug("json = {}", json);

            final byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            messageProperties.setContentEncoding("UTF-8");
            messageProperties.setContentLength(bytes.length);
            messageProperties.setTimestamp(new Date());
            messageProperties.setType(object.getClass().getName());
            if (messageProperties.getMessageId() == null) {
                messageProperties.setMessageId(UUID.randomUUID().toString());
            }

            return new Message(bytes, messageProperties);
        } catch (final Exception e) {
            throw new MessageConversionException(e.getMessage(), e);
        }
    }

    @Override
    public Object fromMessage(final Message message) throws MessageConversionException {
        final byte[] messageBody = message.getBody();
        if (messageBody == null) {
            LOG.warn("No message body found for message: {}", message);
            return null;
        }

        final MessageProperties messageProperties = message.getMessageProperties();
        final String className = StringUtils.trimAllWhitespace(messageProperties.getType());
        if (StringUtils.isEmpty(className)) {
            LOG.error("Could not determine class from message: {}", message);
            return null;
        }

        try {
            final String json = new String(messageBody, StandardCharsets.UTF_8);
            LOG.debug("json = {}", json);
            return GSON.fromJson(json, Class.forName(className));
        } catch (final Exception e) {
            LOG.error("Could not deserialize message: " + message, e);
            throw new MessageConversionException(e.getMessage(), e);
        }
    }
}
