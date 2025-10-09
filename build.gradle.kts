import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.ben-manes.versions") version "0.51.0"
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.spring") version "2.2.20"
    kotlin("plugin.jpa") version "2.2.20"
}

repositories {
    mavenCentral()
}

group = "de.babsek.demo.axontesting"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    implementation("com.opencqrs:framework-spring-boot-starter:1.0.0")

    runtimeOnly("org.postgresql:postgresql:42.7.8")
    implementation("com.vladmihalcea:hibernate-types-60:2.21.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.opencqrs:framework-test:1.0.0")
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
