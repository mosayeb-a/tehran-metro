import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.ma.tehro"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ma.tehro"
        minSdk = 21
        targetSdk = 36
        versionCode = 5
        versionName = "0.5.0"


        val properties = Properties().apply {
            load(File(rootDir, "local.properties").inputStream())
        }
        val fields = mapOf(
            "github_token" to properties.getProperty("github_token"),
            "stations_gist_id" to properties.getProperty("stations_gist_id"),
            "feedbacks_gist_id" to properties.getProperty("feedbacks_gist_id")
        )
        fields.forEach { (name, value) ->
            buildConfigField("String", name, value)
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
        resourceConfigurations += "en"
    }

    signingConfigs {
        create("release") {
            val localProperties = gradleLocalProperties(rootDir, providers)

            storeFile = file(localProperties.getProperty("store_file"))
            storePassword = localProperties.getProperty("store_password")
            keyAlias = localProperties.getProperty("key_alias")
            keyPassword = localProperties.getProperty("key_password")
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


            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.kotlinx.serialization)

    implementation(libs.androidx.navigation)
    implementation(libs.androidx.navigation.ui)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation(libs.osmdroid.android)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}