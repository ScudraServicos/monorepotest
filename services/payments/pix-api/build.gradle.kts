plugins {
    id("br.com.ume.commons.micronaut-conventions")
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.7.10"
}

group = "br.com.ume"

val emvQrcodeVersion=project.properties.get("emvQrcodeVersion")
val starkBankSdkVersion=project.properties.get("starkBankSdkVersion")

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.mvallim:emv-qrcode:${emvQrcodeVersion}")
    implementation("com.starkbank:sdk:${starkBankSdkVersion}")
}

application {
    mainClass.set("br.com.ume.api.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("19")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "19"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "19"
        }
    }
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



