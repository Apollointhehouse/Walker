package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.Tickable
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.render.Drawable
import dev.apollointhehouse.walker.utils.math.AABB2dc
import dev.apollointhehouse.walker.utils.math.lerpAngle
import org.joml.Vector2d

interface Entity : Drawable, Tickable {
    val pos: Vector2d
    val oldPos: Vector2d
    val velocity: Vector2d
    val oldVelocity: Vector2d

    fun getPos(deltaTime: Double, out: Vector2d = Vector2d()) = oldPos.lerp(pos, deltaTime, out)
    fun getVelocity(deltaTime: Double, out: Vector2d = Vector2d()) = oldVelocity.lerp(velocity, deltaTime, out)
    fun getAngle(deltaTime: Double) = lerpAngle(angleO, angle, deltaTime)

    val x: Double

    val y: Double

    val xo: Double
    val yo: Double

    val dx: Double
    val dy: Double

    val dxo: Double
    val dyo: Double

    val angle: Double
    val angleO: Double

    val bb: AABB2dc

    val level: Level?
}