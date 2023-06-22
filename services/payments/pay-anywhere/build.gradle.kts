plugins {
    id("br.com.ume.commons.micronaut-conventions")
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.7.10"
}

group = "br.com.ume"

repositories {
    mavenCentral()
}

dependencies {
}

application {
    mainClass.set("br.com.ume.api.ApplicationKt")
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("br.com.unit.*")
    }
}