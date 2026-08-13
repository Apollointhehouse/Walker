package dev.apollointhehouse.walker.utils.math

import dev.apollointhehouse.walker.level.Level
import org.joml.Vector2d
import kotlin.math.tan

fun lerpAngle(a: Double, b: Double, t: Double): Double {
    var delta = (b - a) % Math.TAU
    if (delta > Math.PI) delta -= Math.TAU
    if (delta < -Math.PI) delta += Math.TAU
    return a + delta * t
}

class HitResult(val pos: Vector2d, val dist: Double)

fun raycast(initialPos: Vector2d, level: Level, angle: Double): HitResult {
    val (x, y) = initialPos

    var mapX: Int
    var mapY: Int
    var dof = 0

    var rayHX = 0.0
    var rayHY = 0.0

    var rayVX = 0.0
    var rayVY = 0.0

    var xOffset = 0.0
    var yOffset = 0.0

    // Horizontal
    val aTan = -1.0 / tan(angle)

    if (angle > Math.PI) {
        rayHY = (y.toInt() shr 6 shl 6) - 0.0001
        rayHX = (y - rayHY) * aTan + x
        yOffset -= 64
        xOffset -= yOffset * aTan
    }

    if (angle < Math.PI) {
        rayHY = (y.toInt() shr 6 shl 6) + 64.0
        rayHX = (y - rayHY) * aTan + x
        yOffset += 64
        xOffset -= yOffset * aTan
    }

    if (angle == 0.0 || angle == Math.PI) {
        rayHX = x
        rayHY = y

        dof = 8
    }

    while (dof < 8) {
        mapX = rayHX.toInt() shr 6
        mapY = rayHY.toInt() shr 6

        if (mapX < level.mapX && mapY < level.mapY && level.get(mapX, mapY) == 1) {
            dof = 8
        } else {
            rayHX += xOffset
            rayHY += yOffset
            dof += 1
        }
    }

    val hDist: Double = initialPos.distance(rayHX, rayHY)


    // Vertical
    xOffset = 0.0
    yOffset = 0.0
    dof = 0
    val nTan = -tan(angle)

    if (angle > Math.TAU / 4 && angle < 3 * Math.TAU / 4) {
        rayVX = (x.toInt() shr 6 shl 6) - 0.0001
        rayVY = (x - rayVX) * nTan + y
        xOffset -= 64
        yOffset -= xOffset * nTan
    }

    if (angle < Math.TAU / 4 || angle > 3 * Math.TAU / 4) {
        rayVX = (x.toInt() shr 6 shl 6) + 64.0
        rayVY = (x - rayVX) * nTan + y
        xOffset += 64
        yOffset -= xOffset * nTan
    }

    if (angle == 0.0 || angle == Math.PI) {
        rayVX = x
        rayVY = y

        dof = 8
    }

    while (dof < 8) {
        mapX = rayVX.toInt() shr 6
        mapY = rayVY.toInt() shr 6

        if (mapX < level.mapX && mapY < level.mapY && level.get(mapX, mapY) == 1) {
            dof = 8
        } else {
            rayVX += xOffset
            rayVY += yOffset
            dof += 1
        }
    }

    val vDist: Double = initialPos.distance(rayVX, rayVY)


    return if (hDist < vDist) {
        HitResult(Vector2d(rayHX, rayHY), hDist)
    } else {
        HitResult(Vector2d(rayVX, rayVY), vDist)
    }

}