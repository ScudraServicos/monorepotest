plugins {
    id("br.com.ume.commons.common-conventions")
}

group = "br.com.ume"

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("com.google.cloud:google-cloud-logging:3.14.7")
    implementation("org.slf4j:jul-to-slf4j:1.7.30")
}