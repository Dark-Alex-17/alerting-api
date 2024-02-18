package com.alexjclarke.alerting.persistence.dto

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.Date
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
data class Alert(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,

    @NotBlank
    @Size(max = 255)
    val extId: String,

    @Size(max = 255)
    var threshold: String,

    @NotNull
    @Enumerated(EnumType.STRING)
    val type: AlertTypeEnum,

    @Size(max = 255)
    var frequency: String = "",

    var enabled: String = "",

    @Temporal(TemporalType.TIMESTAMP)
    var lastTriggerTimestamp: Date,

    @Temporal(TemporalType.TIMESTAMP)
    var notificationSentTimestamp: Date,

    var isTriggered: Boolean,

    @Size(max = 255)
    var referenceTimePeriod: String,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    val created: Date,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    var updated: Date,

    @Size(max = 255)
    var updatedBy: String,

    @ManyToOne
    var account: Account
)