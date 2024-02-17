package com.poc.alerting.batch;

import java.util.Base64;
import java.util.stream.StreamSupport;

import org.apache.http.client.fluent.Request;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
    ConnectionFactory connectionFactory;
    MessageListenerAdapter accountWorkerListenerAdapter;
    MessageConverter gsonMessageConverter;
    ApplicationEventPublisher applicationEventPublisher;
    ConsumerCreator consumerCreator;

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStartup.class);

    @Override
    public void onApplicationEvent(@NotNull final ApplicationReadyEvent event) {
        LOG.info("Creating consumers for existing queues");
        try {

            final String rabbitMqUrl = String.format("http://%s:15672/api/exchanges/poc/alerting/bindings/source", "localhost");

            //auth is kind of a kluge here.  Apparently the HttpClient Fluent API doesn't support
            //it except by explicitly setting the auth header.
            final String json = Request.Get(rabbitMqUrl)
                                       .connectTimeout(1000)
                                       .socketTimeout(1000)
                                       .addHeader("Authorization", "Basic " + getAuthToken())
                                       .execute().returnContent().asString();

            final JsonParser parser = new JsonParser();
            final JsonArray array = parser.parse(json).getAsJsonArray();

            StreamSupport.stream(array.spliterator(), false)
                         .map(jsonElement -> jsonElement.getAsJsonObject().get("destination").getAsString())
                         .forEach(queueName -> consumerCreator.createConsumer(queueName));
        } catch (final Exception e) {
            LOG.error("Error create consumers for existing queues", e);
        }
    }

    private String getAuthToken() {
        final String basicPlaintext = "poc" + ":" + "s!mpleP@ssw0rd";

        final Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(basicPlaintext.getBytes());
    }
}
