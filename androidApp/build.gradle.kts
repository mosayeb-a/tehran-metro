import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ma.tehro"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    defaultConfig {
        applicationId = "com.ma.tehro"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 11
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
        androidResources.localeFilters += listOf("en")
    }

    val localProperties = gradleLocalProperties(rootDir, providers)

    signingConfigs {
        val releaseKeystorePath = localProperties.getProperty("store_file")?.takeIf { it.isNotBlank() }
        val fallbackKeystore = File(rootDir, "debug.keystore")

        if (releaseKeystorePath != null) {
            val releaseKeystore = file(releaseKeystorePath)
            if (releaseKeystore.exists()) {
                create("release") {
                    storeFile = releaseKeystore
                    storePassword = localProperties.getProperty("store_password")
                    keyAlias = localProperties.getProperty("key_alias")
                    keyPassword = localProperties.getProperty("key_password")
                }
            }
        }

        if (signingConfigs.findByName("release") == null && fallbackKeystore.exists()) {
            create("fallback") {
                storeFile = fallbackKeystore
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = when {
                signingConfigs.findByName("release") != null -> {
                    println("using real signing config for release.")
                    signingConfigs.getByName("release")
                }
                signingConfigs.findByName("fallback") != null -> {
                    println("using fallback debug signing for release.")
                    signingConfigs.getByName("fallback")
                }
                else -> {
                    println("no signing config found. release may fail.")
                    null
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.material3.android)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)

    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.osmdroid.android)

    implementation(projects.shared)
}