package com.alexjclarke.alerting.batch.jobs

import com.alexjclarke.alerting.persistence.repositories.AlertRepository
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.Date

@Component
open class AlertQueryJob @Autowired constructor(
    private val alertRepository: AlertRepository
): Job {
    companion object {
        const val ALERT_ID = "alertId"
        const val CRON = "cron"
        const val ACCOUNT_ID = "accountId"
    }

    override fun execute(context: JobExecutionContext) {
        val data = context.jobDetail.jobDataMap
        val alertId = data.getString(ALERT_ID)
        val cron = data.getString(CRON)
        val accountId = data.getString(ACCOUNT_ID)

        val alert = alertRepository.findByExtIdAndAccount_ExtId(alertId, accountId)

        with(alert) {
            val queryResult = type.query()
            println("PERFORMING QUERY for $alertId-$accountId. Running with the following CRON expression: $cron")
            if (queryResult >= threshold.toInt()) {
                val currentTime = Date()
                if (!isTriggered) {
                    isTriggered = true
                }

                notificationSentTimestamp.let {
                    if (Duration.between(notificationSentTimestamp.toInstant(), currentTime.toInstant()).toSeconds() >= 15) {
                        println("Alert Triggered!!!!!!!!!!!!!")
                        notificationSentTimestamp = currentTime
                    }
                }

                lastTriggerTimestamp = currentTime
                alertRepository.save(this)
            }
        }
    }
}