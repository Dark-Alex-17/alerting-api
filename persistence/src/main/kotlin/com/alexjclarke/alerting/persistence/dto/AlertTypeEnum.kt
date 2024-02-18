package com.alexjclarke.alerting.persistence.dto

import kotlin.random.Random

enum class AlertTypeEnum(val query: () -> Int) {
    HARDCODED_ALERT_1({ Random.nextInt(0,1000) }),
    HARDCODED_ALERT_2({ Random.nextInt(0,1000) })
}