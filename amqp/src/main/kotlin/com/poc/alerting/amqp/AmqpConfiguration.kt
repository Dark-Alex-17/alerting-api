package com.poc.alerting.amqp

import org.apache.commons.lang3.time.DateUtils.MILLIS_PER_MINUTE
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Exchange
import org.springframework.amqp.core.ExchangeBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["com.poc.alerting.amqp"])
open class AmqpConfiguration {
    companion object {
        const val AMQP_NAME = "poc_alerting"
        const val NEW_ACCOUNT = "new_account"
        const val FANOUT_NAME = "${AMQP_NAME}_fanout"
    }

    @Bean
    open fun connectionFactory(): ConnectionFactory {
        return CachingConnectionFactory().apply {
            virtualHost = "poc"
            username = "poc_user"
            setPassword("s!mpleP@ssw0rd")
            setAddresses("localhost")
        }
    }

    @Bean
    open fun newAccountQueue(): Queue {
        return Queue(NEW_ACCOUNT, true)
    }

    @Bean
    open fun newAccountExchange(): FanoutExchange {
        return FanoutExchange(NEW_ACCOUNT)
    }

    @Bean
    open fun newAccountBinding(newAccountQueue: Queue, newAccountExchange: FanoutExchange): Binding {
        return BindingBuilder.bind(newAccountQueue).to(newAccountExchange)
    }

    @Bean
    open fun pocAlertingExchange(): FanoutExchange {
        return FanoutExchange(FANOUT_NAME)
    }

    @Bean
    open fun directExchange(): DirectExchange {
        return ExchangeBuilder.directExchange(AMQP_NAME)
            .durable(false)
            .withArgument("alternate-exchange", NEW_ACCOUNT)
            .build()
    }

    @Bean
    open fun exchangeBinding(directExchange: Exchange, pocAlertingExchange: FanoutExchange): Binding {
        return BindingBuilder.bind(directExchange).to(pocAlertingExchange)
    }

    @Bean
    open fun rabbitTemplate(connectionFactory: ConnectionFactory, gsonMessageConverter: GsonMessageConverter): RabbitTemplate {
        return RabbitTemplate(connectionFactory).apply {
            setExchange(FANOUT_NAME)
            messageConverter = gsonMessageConverter
            setReplyTimeout(MILLIS_PER_MINUTE)
            setMandatory(true)
        }
    }
}