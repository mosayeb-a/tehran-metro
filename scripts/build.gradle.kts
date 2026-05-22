import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

group = "app.ma"
version = "1.0"
//
//repositories {
//    mavenCentral()
//    google()
//}

kotlin {
    jvmToolchain(11)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    testImplementation(libs.junit)

    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation(libs.kotlinx.serialization)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.coroutines.core)
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")

    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")

    implementation("org.jsoup:jsoup:1.17.2")

    implementation(project(":shared"))
}