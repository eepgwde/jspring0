/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        setAllowInsecureProtocol(true)        
        url = uri("http://caeneus.fritz.box:8081/repository/caeneus-3/")
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa:2.6.1")
    api("org.springframework.boot:spring-boot-starter-web:2.6.1")
    runtimeOnly("com.h2database:h2:1.4.200")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.1")
}

group = "org.artikus"
version = "0.0.1-SNAPSHOT"
description = "sprng"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
