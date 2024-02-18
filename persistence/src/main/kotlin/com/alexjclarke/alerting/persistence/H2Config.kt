package com.alexjclarke.alerting.persistence

import org.h2.tools.Server
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories("com.alexjclarke.alerting.persistence.repositories")
@EntityScan("com.alexjclarke.alerting.persistence.dto")
@EnableAutoConfiguration
open class H2Config {
    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile("persistence")
    open fun inMemoryH2DatabaseServer(): Server {
        return Server.createTcpServer(
            "-tcp", "-tcpAllowOthers", "-tcpPort", "9091"
        )
    }
}