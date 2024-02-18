package com.alexjclarke.alerting.persistence.repositories

import com.alexjclarke.alerting.persistence.dto.Recipient
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RecipientRepository : CrudRepository<Recipient, Long> {
    fun findByExtIdAndAlert_ExtIdAndAccount_ExtId(recipientId: String, alertId: String, accountExtId: String): Recipient

    fun findAllByAccount_ExtId(accountExtId: String): List<Recipient>

    fun findAllByAlert_ExtIdAndAccount_ExtId(alertId: String, accountExtId: String): List<Recipient>
}