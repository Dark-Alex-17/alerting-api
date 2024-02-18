import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.flywaydb.flyway") version "7.9.1"
    kotlin("plugin.jpa") version "1.5.10"
}

dependencies {
    "implementation"("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.flywaydb:flyway-core")
    implementation("org.apache.tomcat:tomcat-jdbc")
    implementation("com.h2database:h2:1.4.200")
}

flyway {
    url = "jdbc:h2:mem:alerting"
    user = "defaultUser"
    password = "secret"
    locations = arrayOf("classpath:resources/db/migration")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
    mainClass.set("com.alexjclarke.alerting.persistence.Persistence")
}
