package io.github.tcrawford.versioning.kotest

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

infix fun <T : Any> Iterable<T>.shouldOnlyHave(t: T): Iterable<T> =
    this.map { it shouldBe t }

infix fun Iterable<String>.shouldOnlyContain(t: String): Iterable<String?> =
    this.map { it shouldContain t }
