plugins {
    kotlin("jvm") version "1.9.10"
    id("com.gradle.plugin-publish") version "1.2.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

group = "com.figure.gradle.semver"
version = "0.0.6"

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    implementation(libs.jgit)
    implementation(libs.kotlin.semver)
    implementation(libs.kotlin.result)
    implementation(libs.plexus.utils)

    testImplementation(gradleTestKit())
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.datatest)
}

tasks.test {
    useJUnitPlatform()
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

kotlin {
    jvmToolchain(11)
}

gradlePlugin {
    plugins {
        create(project.name) {
            id = "com.figure.gradle.settings.semver"
            displayName = "Semver Settings Plugin"
            description = "Semver Settings Plugin"
            implementationClass = "com.figure.gradle.semver.SemverSettingsPlugin"
        }
    }
}
