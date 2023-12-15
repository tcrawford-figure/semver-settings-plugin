package io.github.tcrawford.gradle.semver.utils

import io.kotest.matchers.shouldBe

infix fun <T : Any> Iterable<T>.shouldOnlyContain(t: T): Iterable<T> =
    this.map { it shouldBe t }
