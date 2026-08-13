package dev.apollointhehouse.walker.utils.math

fun lerpAngle(a: Double, b: Double, t: Double): Double {
    var delta = (b - a) % Math.TAU
    if (delta > Math.PI) delta -= Math.TAU
    if (delta < -Math.PI) delta += Math.TAU
    return a + delta * t
}