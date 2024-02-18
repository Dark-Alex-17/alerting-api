package com.alexjclarke.alerting.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.alexjclarke.alerting"])
open class AlertingApi

fun main(args: Array<String>) {
    runApplication<AlertingApi>(*args)
}