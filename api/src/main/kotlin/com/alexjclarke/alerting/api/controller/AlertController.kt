package com.alexjclarke.alerting.api.controller

import com.alexjclarke.alerting.amqp.AlertingAmqpMessage.Add
import com.alexjclarke.alerting.amqp.AlertingAmqpMessage.Delete
import com.alexjclarke.alerting.amqp.AlertingAmqpMessage.Pause
import com.alexjclarke.alerting.amqp.AlertingAmqpMessage.Resume
import com.alexjclarke.alerting.amqp.AlertingAmqpMessage.Update
import com.alexjclarke.alerting.amqp.RabbitSender
import com.alexjclarke.alerting.api.PropertyCopier
import com.alexjclarke.alerting.persistence.dto.Alert
import com.alexjclarke.alerting.persistence.repositories.AccountRepository
import com.alexjclarke.alerting.persistence.repositories.AlertRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@Validated
open class AlertController @Autowired constructor(
    private val accountRepository: AccountRepository,
    private val alertRepository: AlertRepository,
    private val rabbitSender: RabbitSender
) {
    @PostMapping("/accounts/{account_id}/alerts")
    open fun addAlert(@PathVariable("account_id") accountId: String,
                 @RequestBody body: @Valid Alert
    ): ResponseEntity<Alert> {
        val account = accountRepository.findByExtId(accountId)
        body.account = account
        val alert = alertRepository.save(body).also {
            rabbitSender.send(Add(body.frequency, body.extId, accountId))
        }

        return ResponseEntity(alert, HttpStatus.CREATED)
    }

    @DeleteMapping("/accounts/{account_id}/alerts/{alert_id}")
    open fun deleteAlert(@PathVariable("account_id") accountId: String,
                    @PathVariable("alert_id") alertId: String
    ): ResponseEntity<Void> {
        rabbitSender.send(Delete(alertId, accountId))
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/accounts/{account_id}/alerts/{alert_id}")
    open fun getAlertById(
        @PathVariable("account_id") accountId: String,
        @PathVariable("alert_id") alertId: String,
        @RequestParam(value = "include_recipients", required = false, defaultValue = "false") includeRecipients: @Valid Boolean?
    ): ResponseEntity<Alert> {
        return ResponseEntity.ok(alertRepository.findByExtIdAndAccount_ExtId(alertId, accountId))
    }

    @GetMapping("/accounts/{account_id}/alerts")
    open fun getListOfAlerts(
        @PathVariable("account_id") accountId: String,
        @RequestParam(value = "include_recipients", required = false, defaultValue = "false") includeRecipients: @Valid Boolean?
    ): ResponseEntity<List<Alert>> {
        return ResponseEntity.ok(alertRepository.findAllByAccount_ExtId(accountId))
    }

    @PatchMapping("/accounts/{account_id}/alerts/{alert_id}")
    open fun updateAlert(
        @PathVariable("account_id") accountId: String,
        @PathVariable("alert_id") alertId: String,
        @RequestBody body: @Valid Alert
    ): ResponseEntity<Alert> {
        val existingAlert = alertRepository.findByExtIdAndAccount_ExtId(alertId, accountId)

        rabbitSender.send(Update(body.frequency, alertId, accountId))

        body.enabled.let {
            if (body.enabled.isNotBlank() && body.enabled.toBoolean() != existingAlert.enabled.toBoolean()) {
                if (body.enabled.toBoolean()) {
                    rabbitSender.send(Resume(alertId, accountId))
                } else {
                    rabbitSender.send(Pause(alertId, accountId))
                }
            }
        }

        PropertyCopier.copyNonNullProperties(body, existingAlert, null)
        return ResponseEntity.ok(alertRepository.save(existingAlert))
    }
}