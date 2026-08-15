package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.Tickable
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.render.Drawable
import dev.apollointhehouse.walker.utils.math.AABB2d
import org.joml.Vector2d

interface Entity : Drawable, Tickable {
    val x: Double
    val y: Double

    val position: Vector2d

    val xo: Double
    val yo: Double

    val dx: Double
    val dy: Double

    val dxo: Double
    val dyo: Double

    val angle: Double

    val bb: AABB2d

    val level: Level?
}