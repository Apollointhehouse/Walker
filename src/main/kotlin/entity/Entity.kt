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

    val x get() = pos.x()
    val y get() = pos.y()

    val xo get() = oldPos.x()
    val yo get() = oldPos.y()

    val dx get() = velocity.x()
    val dy get() = velocity.y()

    val dxo get() = oldVelocity.x()
    val dyo get() = oldVelocity.x()

    val angle: Double
    val angleO: Double

    val bb: AABB2dc

    var level: Level?
}