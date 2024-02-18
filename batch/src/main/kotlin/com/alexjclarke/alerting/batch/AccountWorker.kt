package com.alexjclarke.alerting.batch

import com.alexjclarke.alerting.amqp.AlertingAmqpMessage
import com.alexjclarke.alerting.amqp.AlertingAmqpMessage.Add
import com.alexjclarke.alerting.amqp.AlertingAmqpMessage.Delete
import com.alexjclarke.alerting.amqp.AlertingAmqpMessage.Pause
import com.alexjclarke.alerting.amqp.AlertingAmqpMessage.Resume
import com.alexjclarke.alerting.amqp.AlertingAmqpMessage.Update
import com.alexjclarke.alerting.batch.jobs.AlertQueryJob
import com.alexjclarke.alerting.batch.jobs.AlertQueryJob.Companion.ACCOUNT_ID
import com.alexjclarke.alerting.batch.jobs.AlertQueryJob.Companion.ALERT_ID
import com.alexjclarke.alerting.batch.jobs.AlertQueryJob.Companion.CRON
import com.alexjclarke.alerting.persistence.dto.Alert
import com.alexjclarke.alerting.persistence.repositories.AlertRepository
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.util.TimeZone

@Service
open class AccountWorker @Autowired constructor(
    private val alertRepository: AlertRepository,
    private val scheduler: Scheduler
){
    fun processMessage(message: AlertingAmqpMessage) {
        when (message) {
            is Add -> createJob(message.alertId, message.accountId, message.frequency)
            is Update -> updateJob(message.alertId, message.accountId, message.frequency)
            is Delete -> deleteJob(message.alertId, message.accountId)
            is Pause -> pauseJob(message.alertId, message.accountId)
            is Resume -> resumeJob(message.alertId, message.accountId)
        }
    }

    private fun createJob(alertId: String, accountId: String, cron: String): Alert {
        val jobDetail = buildJob(alertId, accountId, cron)
        val trigger = createTrigger(alertId, jobDetail, cron)

        with (scheduler) {
            scheduleJob(jobDetail, trigger)
            start()
        }

        return alertRepository.findByExtIdAndAccount_ExtId(alertId, accountId)
    }

    private fun updateJob(alertId: String, accountId: String, cron: String): Alert {
        scheduler.deleteJob(JobKey.jobKey(alertId, accountId))
        return createJob(alertId, accountId, cron)
    }

    private fun deleteJob(alertId: String, accountId: String): Alert {
        val alert = alertRepository.findByExtIdAndAccount_ExtId(alertId, accountId)
        scheduler.deleteJob(JobKey.jobKey(alertId, accountId))
        alertRepository.delete(alert)
        return alert
    }

    private fun pauseJob(alertId: String, accountId: String): Alert {
        scheduler.pauseJob(JobKey.jobKey(alertId, accountId))
        return alertRepository.findByExtIdAndAccount_ExtId(alertId, accountId)
    }

    private fun resumeJob(alertId: String, accountId: String): Alert {
        scheduler.resumeJob(JobKey.jobKey(alertId, accountId))
        return alertRepository.findByExtIdAndAccount_ExtId(alertId, accountId)
    }

    private fun buildJob(alertId: String, accountId: String, cron: String): JobDetail {
        val jobDataMap = JobDataMap()
        jobDataMap[ALERT_ID] = alertId
        jobDataMap[ACCOUNT_ID] = accountId
        jobDataMap[CRON] = cron
        return JobBuilder.newJob().ofType(AlertQueryJob::class.java)
            .storeDurably()
            .withIdentity(alertId, accountId)
            .usingJobData(jobDataMap)
            .build()
    }

    private fun createTrigger(alertId: String, jobDetail: JobDetail, cron: String): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity("${alertId}_trigger")
            .withSchedule(
                CronScheduleBuilder.cronSchedule(cron)
                    .withMisfireHandlingInstructionFireAndProceed()
                    .inTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
            )
            .usingJobData("cron", cron)
            .build()
    }
}