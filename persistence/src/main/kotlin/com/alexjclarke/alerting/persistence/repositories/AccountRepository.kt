package com.alexjclarke.alerting.persistence.repositories

import com.alexjclarke.alerting.persistence.dto.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, Long> {
    fun findByExtId(accountExtId: String): Account
}