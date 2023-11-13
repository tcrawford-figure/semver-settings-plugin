import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
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
