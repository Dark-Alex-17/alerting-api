package com.poc.alerting.batch

import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication(scanBasePackages = ["com.poc.alerting"])
open class BatchWorker

fun main(args: Array<String>) {
    SpringApplicationBuilder().sources(BatchWorker::class.java)
        .bannerMode(Banner.Mode.OFF)
        .web(WebApplicationType.NONE)
        .run(*args)
}