package com.figure.gradle.semver.internal.extensions

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.support.serviceOf
import java.io.File

internal val Settings.objects
    get() = serviceOf<ObjectFactory>()

internal fun ObjectFactory.regularFile(file: File): RegularFileProperty =
    fileProperty().apply { set(file) }
