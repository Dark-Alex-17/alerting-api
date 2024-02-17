package com.poc.alerting.batch

import com.poc.alerting.amqp.AmqpConfiguration.Companion.NEW_ACCOUNT
import com.poc.alerting.amqp.GsonMessageConverter
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.net.InetAddress
import java.util.concurrent.Executor

@Configuration
@EnableAsync
open class WorkerConfiguration: AsyncConfigurer {
    companion object {
        const val NEW_CONSUMER = "new_consumer"
    }

    @Bean("asyncExecutor")
    override fun getAsyncExecutor(): Executor {
        return ThreadPoolTaskExecutor()
    }

    @Bean
    open fun rabbitAdmin(connectionFactory: ConnectionFactory): RabbitAdmin {
        return RabbitAdmin(connectionFactory)
    }

    @Bean
    open fun queueCreatorListenerAdapter(queueCreator: QueueCreator, gsonMessageConverter: GsonMessageConverter): MessageListenerAdapter {
        return MessageListenerAdapter(queueCreator, "createQueue").apply {
            setMessageConverter(gsonMessageConverter)
        }
    }

    @Bean
    open fun queueCreatorContainer(connectionFactory: ConnectionFactory, queueCreatorListenerAdapter: MessageListenerAdapter,
                                   gsonMessageConverter: GsonMessageConverter): SimpleMessageListenerContainer {
        return SimpleMessageListenerContainer(connectionFactory).apply {
            setExclusive(true)
            setExclusiveConsumerExceptionLogger(ExclusiveConsumerExceptionLogger())
            setQueueNames(NEW_ACCOUNT)
            setMessageListener(queueCreatorListenerAdapter)
            setDefaultRequeueRejected(false)
        }
    }

    @Bean
    open fun newConsumerExchange(): FanoutExchange {
        return FanoutExchange(NEW_CONSUMER)
    }

    @Bean
    open fun newConsumerQueue(): Queue {
        return Queue("${NEW_CONSUMER}_${InetAddress.getLocalHost().hostName}", true)
    }

    @Bean
    open fun newConsumerBinding(newConsumerQueue: Queue, newConsumerExchange: FanoutExchange): Binding {
        return BindingBuilder.bind(newConsumerQueue).to(newConsumerExchange)
    }

    @Bean
    open fun consumerCreatorListenerAdapter(consumerCreator: ConsumerCreator, gsonMessageConverter: GsonMessageConverter): MessageListenerAdapter {
        return MessageListenerAdapter(consumerCreator, "createConsumer").apply {
            setMessageConverter(gsonMessageConverter)
        }
    }

    @Bean
    open fun consumerCreatorContainer(connectionFactory: ConnectionFactory, consumerCreatorListenerAdapter: MessageListenerAdapter,
                                      newConsumerQueue: Queue): SimpleMessageListenerContainer {
        return SimpleMessageListenerContainer(connectionFactory).apply {
            setExclusive(true)
            setExclusiveConsumerExceptionLogger(ExclusiveConsumerExceptionLogger())
            setQueues(newConsumerQueue)
            setMessageListener(consumerCreatorListenerAdapter)
            setDefaultRequeueRejected(false)
        }
    }

    @Bean
    open fun accountWorkerListenerAdapter(accountWorker: AccountWorker, gsonMessageConverter: GsonMessageConverter): MessageListenerAdapter {
        return MessageListenerAdapter(accountWorker, "processMessage").apply {
            setMessageConverter(gsonMessageConverter)
        }
    }
}