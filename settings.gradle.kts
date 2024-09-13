rootProject.name = "git-aware-versioning-plugin"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.develocity") version "3.17.6"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        val isCi = providers.environmentVariable("GITHUB_ACTIONS")
        publishing.onlyIf { isCi.isPresent || it.buildResult.failures.isNotEmpty() }
    }
}
