package dev.apollointhehouse.walker.utils.math

import org.joml.Vector2d
import org.joml.primitives.Rectangledc

interface AABB2dc : Rectangledc {
    operator fun plus(point: Vector2d): AABB2d {
        val out = AABB2d()
        translate(point, out)
        return out
    }
}