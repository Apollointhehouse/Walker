package dev.apollointhehouse.walker.utils

object MathHelper {
    fun lerp(start: Double, end: Double, fraction: Double): Double {
        return start + fraction * (end - start)
    }
}