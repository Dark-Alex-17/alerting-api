import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
	"implementation"(project(":persistence"))
	"implementation"(project(":amqp"))

	implementation("org.slf4j:slf4j-api")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.amqp:spring-amqp")
	implementation("org.springframework.amqp:spring-rabbit")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-quartz")
	implementation("org.springframework:spring-jdbc")
	implementation("org.projectlombok:lombok")
	implementation("org.apache.commons:commons-lang3")
	implementation("org.apache.httpcomponents:fluent-hc")
	implementation("com.google.code.gson:gson")

	annotationProcessor("org.projectlombok:lombok")
}

tasks.getByName<BootJar>("bootJar") {
	enabled = true
	mainClass.set("com.alexjclarke.alerting.batch.BatchWorkerKt")
}

springBoot {
	mainClass.set("com.alexjclarke.alerting.batch.BatchWorkerKt")
}
