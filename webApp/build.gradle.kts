@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
kotlin {
    wasmJs {
        outputModuleName.set("tehren_metro")
        browser {
            commonWebpackConfig {
                outputFileName = "tehro.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        add(project.projectDir.path)
                    }
                }
            }
            compilerOptions {
                sourceMap.set(true)
                sourceMapEmbedSources.set(JsSourceMapEmbedMode.SOURCE_MAP_SOURCE_CONTENT_ALWAYS)
            }
        }
        binaries.executable()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)
                implementation(libs.kotlinx.browser)

                implementation(projects.shared)

                implementation(libs.compose.navigation)

                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                implementation(libs.kotlinx.serialization)

                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.androidx.lifecycle.viewModel)
            }
        }
    }
}