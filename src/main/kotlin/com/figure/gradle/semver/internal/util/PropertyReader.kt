package com.figure.gradle.semver.internal.util

import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

internal class PropertyReader
@Inject constructor(providers: ProviderFactory) {

    val stage: Provider<String> = providers.gradleProperty("semver.stage")
    val modifier: Provider<String> = providers.gradleProperty("semver.modifier")
    val tagPrefix = providers.gradleProperty("semver.tagPrefix")
}
