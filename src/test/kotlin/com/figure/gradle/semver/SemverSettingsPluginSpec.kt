package com.figure.gradle.semver

import com.figure.gradle.semver.testkit.GradleProjectListener
import com.figure.gradle.semver.util.resolveResourceDirectory
import io.kotest.core.spec.style.FunSpec

class SemverSettingsPluginSpec : FunSpec({

    val gradleProjectListener = GradleProjectListener(resolveResourceDirectory("sample"))

    listener(gradleProjectListener)

    test("should have git dir") {
        val runner = gradleProjectListener.initGradleRunner()

        runner.build()
    }
})
