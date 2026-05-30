@file:OptIn(ExperimentalWasmDsl::class)

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.sqlDelight)
}

kotlin {
    jvm()

    androidLibrary {
        namespace = "com.ma.tehro.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()

        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    wasmJs {
        browser {
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.material3)
            implementation(libs.material.icons.extended)

            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.datetime)

            implementation(libs.compose.navigation)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            api(libs.multiplatform.settings)
            api(libs.multiplatform.settings.coroutines)
            api(libs.multiplatform.settings.make.observable)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.ktor.core)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.logging)

            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewModel)

            implementation(libs.kotlinx.coroutines.core)

            implementation("io.github.pdvrieze.xmlutil:core:1.0.0-rc2")

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
        }

        androidMain {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)

                implementation(libs.koin.android)

                implementation(libs.ktor.android)

                implementation(libs.multiplatform.settings.datastore)
                implementation(libs.androidx.datastore.preferences)

                implementation(libs.osmdroid.android)
//                implementation("org.maplibre.gl:android-sdk:11.11.0")

                implementation(libs.sqldelight.android.driver)
            }
        }

        wasmJsMain {
            dependencies {
                implementation(libs.ktor.js)
                implementation(libs.kotlinx.browser)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

sqldelight {
    databases {
        create("TehroDatabase") {
            packageName.set("com.ma.thero.db")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/schemas"))
            migrationOutputDirectory.set(file("src/commonMain/sqldelight/migrations"))
            verifyMigrations.set(true)
            generateAsync = true
        }
    }
}

buildkonfig {
    packageName = "com.ma.tehro.shared"

    defaultConfigs {
        val localProperties = Properties().apply {
            val localPropsFile = rootProject.file("local.properties")
            if (localPropsFile.exists()) {
                load(localPropsFile.reader())
            }
        }

        buildConfigField(
            type = STRING,
            name = "GITHUB_TOKEN",
            value = localProperties.getProperty("github_token", "")
        )
        buildConfigField(
            type = STRING,
            name = "STATIONS_GIST_ID",
            value = localProperties.getProperty("stations_gist_id", "")
        )
        buildConfigField(
            type = STRING,
            name = "FEEDBACKS_GIST_ID",
            value = localProperties.getProperty("feedbacks_gist_id", "")
        )
        buildConfigField(
            type = STRING,
            name = "VERSION_NAME",
            value = "1.0.0"
        )
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.ma.thero.resources"
    generateResClass = always
}