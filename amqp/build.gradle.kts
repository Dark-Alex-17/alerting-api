import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    implementation("org.springframework.amqp:spring-amqp")
    implementation("org.springframework.amqp:spring-rabbit")
    implementation("com.google.code.gson:gson")
    implementation("org.projectlombok:lombok")
    implementation("org.apache.commons:commons-lang3")

    annotationProcessor("org.projectlombok:lombok")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
