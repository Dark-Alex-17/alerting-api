package com.poc.alerting.persistence.repositories

import com.poc.alerting.persistence.dto.Alert
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface AlertRepository : CrudRepository<Alert, Long>, PagingAndSortingRepository<Alert, Long> {
    fun findByExtIdAndAccount_ExtId(alertId: String, accountExtId: String): Alert

    fun findAllByAccount_ExtId(accountExtId: String): List<Alert>
}
