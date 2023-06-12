import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project

val libs = the<LibrariesForLibs>()

println("testedrive")
println(libs)

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.kapt")
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    kapt(libs.micronautDataProcessor)
    kapt(libs.micronautHttpValidation)
    kapt(libs.micronautOpenApi)

    implementation(libs.micronautHttpClient)
    implementation(libs.micronautJacksonDataBind)
    implementation(libs.micronautKotlinRuntime)
    implementation(libs.jakartaAnnotationApi)
    implementation(libs.kotlinReflect)
    implementation(libs.kotlinStdlibJdk8)
    implementation(libs.micronautDataJdbc)
    implementation(libs.micronautValidation)
    implementation(libs.julToSl4j)
    implementation(libs.micronautLiquibase)
    implementation(libs.micronautJdbcHikari)
    implementation(libs.logbackJsonClassic)
    implementation(libs.logbackJackson)
    implementation(libs.micronautReactor)
    implementation(libs.micronautReactorHttpClient)
    implementation(libs.rxJava2)
    implementation(libs.gson)
    implementation(libs.micronautManagement)
    implementation(libs.micronautDataHibernateJpa)
    implementation(libs.swaggerAnnotations)
    implementation(libs.googleCloudPubSub)
    implementation(libs.googleCloudLogging)
    implementation(libs.grpcNetty)
    implementation(libs.jacksonModuleKotlin)
    implementation(libs.jacksonDatatypeJsr310)
    implementation(project(":libs:gcp-structured-logging"))

    testImplementation(libs.mockito)
    testImplementation(libs.mockitoKotlin)
    testImplementation(libs.micronautTestJunit)
    testImplementation(libs.micronautTestRestAssured)
    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.junitJupiterEngine)

    testAnnotationProcessor(libs.micronautInjectJava)

    testRuntimeOnly(libs.junitJupiterEngine)

    runtimeOnly(libs.logbackClassic)
    runtimeOnly(libs.postgresql)
}