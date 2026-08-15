package dev.apollointhehouse.walker.utils.math

import org.joml.Vector2d
import org.joml.minus
import org.joml.plus
import org.joml.times

data class AABB2d(
    val min: Vector2d,
    val max: Vector2d
) {
    operator fun contains(point: Vector2d): Boolean {
        return point.x in min.x..max.x && point.y in min.y..max.y
    }

    fun intersects(other: AABB2d): Boolean {
        return min.x <= other.max.x && max.x >= other.min.x &&
                min.y <= other.max.y && max.y >= other.min.y
    }

    operator fun plus(vec: Vector2d): AABB2d {
        return AABB2d(min + vec, max + vec)
    }

    operator fun minus(vec: Vector2d): AABB2d {
        return AABB2d(min + vec, max - vec)
    }

    operator fun div(scalar: Double): AABB2d {
        return AABB2d(min / scalar, max / scalar)
    }

    operator fun times(scalar: Vector2d): AABB2d {
        return AABB2d(min * scalar, max * scalar)
    }
}
