package dev.apollointhehouse.walker.utils.math

import org.joml.Vector2d
import org.joml.primitives.Rectangled

class AABB2d(
    min: Vector2d = Vector2d(),
    max: Vector2d = Vector2d()
) : AABB2dc, Rectangled(min, max)