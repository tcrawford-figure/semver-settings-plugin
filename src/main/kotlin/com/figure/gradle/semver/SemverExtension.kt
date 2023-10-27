package com.figure.gradle.semver

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class SemverExtension @Inject constructor(
    objects: ObjectFactory,
    settings: Settings,
) {
    val rootProjectDir: RegularFileProperty = objects.fileProperty().convention { settings.rootDir }
    val initialVersion: Property<String> = objects.property<String>().convention("0.0.0")
}
