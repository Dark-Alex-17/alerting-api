package com.poc.alerting.api.controller

import com.poc.alerting.api.PropertyCopier
import com.poc.alerting.persistence.dto.Recipient
import com.poc.alerting.persistence.repositories.RecipientRepository
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
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@Validated
open class RecipientController @Autowired constructor(
    private val recipientRepository: RecipientRepository
) {
    @PostMapping("/accounts/{account_id}/alerts/{alert_id}/recipients")
    open fun addRecipient(@PathVariable("account_id") accountId: String,
                     @PathVariable("alert_id") alertId: String,
                     @RequestBody body: @Valid MutableList<Recipient>
    ): ResponseEntity<Iterable<Recipient>> {
        return ResponseEntity(recipientRepository.saveAll(body), HttpStatus.CREATED)
    }

    @DeleteMapping("/accounts/{account_id}/alerts/{alert_id}/recipients/{recipient_id}")
    open fun deleteRecipient(
        @PathVariable("account_id") accountId: String,
        @PathVariable("alert_id") alertId: String,
        @PathVariable("recipient_id") recipientId: String
    ): ResponseEntity<Void> {
        recipientRepository.delete(recipientRepository.findByExtIdAndAlert_ExtIdAndAccount_ExtId(recipientId, alertId, accountId))
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/accounts/{account_id}/recipients")
    open fun getAllRecipients(
        @PathVariable("account_id") accountId: String
    ): ResponseEntity<List<Recipient>> {
        return ResponseEntity.ok(recipientRepository.findAllByAccount_ExtId(accountId))
    }

    @GetMapping("/accounts/{account_id}/alerts/{alert_id}/recipients/{recipient_id}")
    open fun getRecipient(
        @PathVariable("account_id") accountId: String,
        @PathVariable("alert_id") alertId: String,
        @PathVariable("recipient_id") recipientId: String
    ): ResponseEntity<Recipient> {
        return ResponseEntity.ok(recipientRepository.findByExtIdAndAlert_ExtIdAndAccount_ExtId(recipientId, alertId, accountId))
    }

    @GetMapping("/accounts/{account_id}/alerts/{alert_id}/recipients")
    open fun getRecipientList(
        @PathVariable("account_id") accountId: String,
        @PathVariable("alert_id") alertId: String
    ): ResponseEntity<List<Recipient>> {
        return ResponseEntity.ok(recipientRepository.findAllByAlert_ExtIdAndAccount_ExtId(alertId, accountId))
    }

    @PatchMapping("/accounts/{account_id}/alerts/{alert_id}/recipients/{recipient_id}")
    open fun updateRecipient(
        @PathVariable("account_id") accountId: String,
        @PathVariable("alert_id") alertId: String,
        @PathVariable("recipient_id") recipientId: String,
        @RequestBody body: @Valid Recipient
    ): ResponseEntity<Recipient> {
        val existingRecipient = recipientRepository.findByExtIdAndAlert_ExtIdAndAccount_ExtId(recipientId, alertId, accountId)
        PropertyCopier.copyNonNullProperties(body, existingRecipient, null)
        return ResponseEntity.ok(recipientRepository.save(existingRecipient))
    }
}