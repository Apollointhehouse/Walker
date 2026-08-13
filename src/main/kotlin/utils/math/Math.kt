package dev.apollointhehouse.walker.utils.math

import dev.apollointhehouse.walker.level.Level
import org.joml.Vector2d
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

fun lerpAngle(a: Double, b: Double, t: Double): Double {
    var delta = (b - a) % Math.TAU
    if (delta > Math.PI) delta -= Math.TAU
    if (delta < -Math.PI) delta += Math.TAU
    return a + delta * t
}

fun deltaAngle(a: Double, b: Double): Double =
    ((a - b % Math.TAU) + Math.TAU) % Math.TAU

fun angleRange(angle: Double, min: Double, max: Double): Double =
    ((max - min) / 2.0) * (cos(angle) - (-1.0)) + min

fun fixAngle(angle: Double): Double =
    ((angle % Math.TAU) + Math.TAU) % Math.TAU

sealed class HitResult(val hitPos: Vector2d, val dist: Double) {
    class Horizontal(pos: Vector2d, dist: Double) : HitResult(pos, dist)
    class Vertical(pos: Vector2d, dist: Double) : HitResult(pos, dist)
}

fun raycast(initialPos: Vector2d, level: Level, angle: Double, depth: Int = 16): HitResult {
    val (x, y) = initialPos
    val dirX = cos(angle)
    val dirY = sin(angle)
    val grid = 64.0
    val eps = 0.0001

    // Horizontal grid lines (constant y)
    var rayHX = x
    var rayHY = y
    var hDist = Double.MAX_VALUE

    if (dirY != 0.0) {
        val yOffset = if (dirY > 0) grid else -grid
        val xOffset = yOffset * (dirX / dirY)

        rayHY = if (dirY > 0) (floor(y / grid) + 1) * grid
        else floor(y / grid) * grid - eps
        rayHX = x + (rayHY - y) * (dirX / dirY)

        var dof = 0
        while (dof < depth) {
            val mapX = rayHX.toInt()
            val mapY = rayHY.toInt()
            if (mapX in 0..<level.mapX * level.size && mapY in 0..<level.mapY * level.size && level.get(mapX, mapY) == 1) break
            rayHX += xOffset
            rayHY += yOffset
            dof++
        }
        hDist = initialPos.distance(rayHX, rayHY)
    }

    // Vertical grid lines (constant x)
    var rayVX = x
    var rayVY = y
    var vDist = Double.MAX_VALUE

    if (dirX != 0.0) {
        val xOffset = if (dirX > 0) grid else -grid
        val yOffset = xOffset * (dirY / dirX)

        rayVX = if (dirX > 0) (floor(x / grid) + 1) * grid
        else floor(x / grid) * grid - eps
        rayVY = y + (rayVX - x) * (dirY / dirX)

        var dof = 0
        while (dof < depth) {
            val mapX = rayVX.toInt()
            val mapY = rayVY.toInt()
            if (mapX in 0..<level.mapX * level.size && mapY in 0..<level.mapY * level.size && level.get(mapX, mapY) == 1) break
            rayVX += xOffset
            rayVY += yOffset
            dof++
        }
        vDist = initialPos.distance(rayVX, rayVY)
    }

    return if (hDist < vDist) HitResult.Horizontal(Vector2d(rayHX, rayHY), hDist)
    else HitResult.Vertical(Vector2d(rayVX, rayVY), vDist)
}