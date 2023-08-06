plugins {
    `embedded-kotlin`
    id("com.gradle.plugin-publish") version "1.2.0"
}

group = "com.figure.gradle.semver"
version = "0.0.6"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(libs.jgit)

    testImplementation(gradleTestKit())
    testImplementation(libs.kotest.runner)
}

tasks.test {
    useJUnitPlatform()
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

kotlin {
    jvmToolchain(8)
}

gradlePlugin {
    plugins {
        create(project.name) {
            id = "com.figure.gradle.semver.settings"
            displayName = "Semver Settings Plugin"
            description = "Semver Settings Plugin"
            implementationClass = "com.figure.gradle.semver.SemverSettingsPlugin"
        }
    }
}
