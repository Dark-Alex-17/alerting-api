package com.alexjclarke.alerting.persistence

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["com.alexjclarke.alerting.persistence"])
open class Persistence

fun main(args: Array<String>) {
    runApplication<Persistence>(*args)
}
