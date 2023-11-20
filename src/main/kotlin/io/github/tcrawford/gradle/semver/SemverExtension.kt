package io.github.tcrawford.gradle.semver

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class SemverExtension @Inject constructor(
    objects: ObjectFactory
) {
    companion object {
        operator fun invoke(settings: Settings) =
            settings.extensions.create<SemverExtension>("semver")
    }

    val rootProjectDir: RegularFileProperty = objects.fileProperty()
    val initialVersion: Property<String> = objects.property<String>().convention("0.0.0")
    val mainBranch: Property<String> = objects.property()
    val developmentBranch: Property<String> = objects.property()
}
