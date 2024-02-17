package com.poc.alerting.persistence

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["com.poc.alerting.persistence"])
open class Persistence

fun main(args: Array<String>) {
    runApplication<Persistence>(*args)
}
