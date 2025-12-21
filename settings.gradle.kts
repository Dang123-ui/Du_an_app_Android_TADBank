pluginManagement {
//    repositories {
//        google {
//            content {
//                includeGroupByRegex("com\\.android.*")
//                includeGroupByRegex("com\\.google.*")
//                includeGroupByRegex("androidx.*")
//            }
//        }
//        mavenCentral()
//        gradlePluginPortal()
//    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()           // MUST
        mavenCentral()     // MUST
        // google()
        // mavenCentral()
        maven(url = "https://jitpack.io")
    }
}


rootProject.name = "TAD_Bank_T1"
include(":app")
include(":transitionbutton")
include(":boommenu")
