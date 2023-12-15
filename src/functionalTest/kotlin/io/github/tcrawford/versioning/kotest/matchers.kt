package io.github.tcrawford.versioning.kotest

import io.kotest.matchers.shouldBe

infix fun <T : Any> Iterable<T>.shouldOnlyContain(t: T): Iterable<T> =
    this.map { it shouldBe t }
