import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    `java-library`
    id("org.springframework.boot") version "2.5.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.spring") version "1.5.10"
}

repositories {
    mavenCentral()
}

allprojects {
    group = "com.alexjclarke.alerting"
    version = "0.0.1-SNAPSHOT"

    tasks.withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xextended-compiler-checks")
            jvmTarget = "11"
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "java")
    apply(plugin = "java-library")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    configurations.all {
        resolutionStrategy {
            failOnVersionConflict()
        }
    }

    sourceSets {
        main {
            java.srcDir("src/main/kotlin")
        }
    }
}

springBoot {
    mainClass.set("com.alexjclarke.alerting.persistence.Persistence")
}
