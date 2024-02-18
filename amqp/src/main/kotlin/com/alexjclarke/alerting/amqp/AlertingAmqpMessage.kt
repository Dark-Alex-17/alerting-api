package com.alexjclarke.alerting.amqp

sealed class AlertingAmqpMessage(
    val alertId: String,
    val accountId: String
): AmqpMessage {
    override fun getRoutingKey(): String {
        return "account_$accountId"
    }

    override fun getExchange(): String {
        return "alerting_api"
    }

    class Add(val frequency: String, alertId: String, accountId: String): AlertingAmqpMessage(alertId, accountId)
    class Update(val frequency: String, alertId: String, accountId: String): AlertingAmqpMessage(alertId, accountId)
    class Delete(alertId: String, accountId: String): AlertingAmqpMessage(alertId, accountId)
    class Pause(alertId: String, accountId: String): AlertingAmqpMessage(alertId, accountId)
    class Resume(alertId: String, accountId: String): AlertingAmqpMessage(alertId, accountId)
}