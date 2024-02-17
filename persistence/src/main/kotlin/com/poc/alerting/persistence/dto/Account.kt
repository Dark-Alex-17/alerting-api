package com.poc.alerting.persistence.dto

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
data class Account(
    @Id
    @GeneratedValue
    val id: Long,

    @NotBlank
    @Size(max = 255)
    val name: String,

    @NotBlank
    @Size(max = 255)
    val extId: String
)
