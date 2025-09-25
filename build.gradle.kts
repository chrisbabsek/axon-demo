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

extra["axonVersion"] = "4.9.4"

dependencyManagement {
    imports {
        mavenBom("org.axonframework:axon-bom:${property("axonVersion")}")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("com.h2database:h2")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    implementation("org.axonframework:axon-spring-boot-starter")
    implementation("org.axonframework.extensions.kotlin:axon-kotlin:4.12.0")
    implementation("org.axonframework.extensions.kotlin:axon-kotlin-test:4.12.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    runtimeOnly("org.postgresql:postgresql:42.7.8")
    implementation("com.vladmihalcea:hibernate-types-60:2.21.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.axonframework:axon-test")
    testImplementation("org.assertj:assertj-core:3.26.3")
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
