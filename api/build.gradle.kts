import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("io.swagger.core.v3.swagger-gradle-plugin") version "2.1.9"
    kotlin("plugin.jpa") version "1.5.10"

}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

dependencies {
    "implementation"(project(":persistence"))
    "implementation"(project(":amqp"))

    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-tomcat")
    implementation("javax.validation:validation-api")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
    mainClass.set("com.poc.alerting.api.AlertingApi")
}