package io.gropp.fruehtau.util

import kotlinx.serialization.Serializable

@Serializable
data class Quadruple<out A, out B, out C, out D>(val first: A, val second: B, val third: C, val fourth: D) {
    override fun toString(): String = "($first, $second, $third, $fourth)"
}
