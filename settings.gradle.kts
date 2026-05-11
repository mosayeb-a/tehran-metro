pluginManagement {
    repositories {
//        google {
//            content {
//                includeGroupByRegex("com\\.android.*")
//                includeGroupByRegex("com\\.google.*")
//                includeGroupByRegex("androidx.*")
//            }
//        }
//        mavenCentral()
//        gradlePluginPortal()
        maven("https://maven.myket.ir/")
    }
}
dependencyResolutionManagement {
    repositories {
//        google()
//        mavenCentral()
//        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://maven.myket.ir/")
    }
}

rootProject.name = "tehro"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":androidApp",":webApp")
include(":scripts")
include(":shared")