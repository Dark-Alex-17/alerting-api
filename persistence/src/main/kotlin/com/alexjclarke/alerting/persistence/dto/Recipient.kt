package com.alexjclarke.alerting.persistence.dto

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.Date
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
data class Recipient(
    @Id
    @GeneratedValue
    val id: Long,

    @NotBlank
    @Size(max = 255)
    val extId: String,

    @NotBlank
    @Size(max = 255)
    val recipient: String,

    @NotNull
    @Enumerated(EnumType.STRING)
    val type: RecipientTypeEnum,

    @Size(max = 255)
    val username: String,

    @Size(max = 255)
    val password: String,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    val created: Date,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    val updated: Date,

    @Size(max = 255)
    val updatedBy: String,

    @ManyToOne
    val alert: Alert,

    @ManyToOne
    val account: Account
)