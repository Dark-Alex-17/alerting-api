package com.poc.alerting.batch

import org.apache.commons.lang3.time.DateUtils
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
open class ConsumerCreator @Autowired constructor(
    private val connectionFactory: ConnectionFactory,
    private val accountWorkerListenerAdapter: MessageListenerAdapter,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val accountConsumerManager: AccountConsumerManager
){
    fun createConsumer(queueName: String) {
        val consumer = SimpleMessageListenerContainer(connectionFactory)
        consumer.setExclusive(true)
        consumer.setExclusiveConsumerExceptionLogger(ExclusiveConsumerExceptionLogger())
        consumer.setQueueNames(queueName)
        consumer.setMessageListener(accountWorkerListenerAdapter)
        consumer.setIdleEventInterval(DateUtils.MILLIS_PER_HOUR)
        consumer.setApplicationEventPublisher(applicationEventPublisher)
        consumer.setDefaultRequeueRejected(false)
        consumer.setAutoDeclare(false)
        consumer.setShutdownTimeout(DateUtils.MILLIS_PER_SECOND * 10)
        accountConsumerManager.registerAndStart(queueName, consumer)
    }
}