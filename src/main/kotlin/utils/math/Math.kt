package dev.apollointhehouse.walker.utils.math

import dev.apollointhehouse.walker.entity.Entity
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.level.tile.TileAir
import org.joml.Vector2d
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

fun lerpAngle(a: Double, b: Double, t: Double): Double {
    var delta = (b - a) % Math.TAU
    if (delta > Math.PI) delta -= Math.TAU
    if (delta < -Math.PI) delta += Math.TAU
    return a + delta * t
}

fun deltaAngle(a: Double, b: Double): Double =
    (((a - b) % Math.TAU) + Math.TAU) % Math.TAU

fun angleRange(angle: Double, min: Double, max: Double): Double =
    ((max - min) / 2.0) * (cos(angle) - (-1.0)) + min

fun fixAngle(angle: Double): Double =
    ((angle % Math.TAU) + Math.TAU) % Math.TAU

fun direction(angle: Double) = Vector2d(
    cos(angle),
    sin(angle)
)

sealed class HitResult(val hitPos: Vector2d) {
    class TileHit(hitPos: Vector2d, val hitType: HitType) : HitResult(hitPos)
    class EntityHit(hitPos: Vector2d, val entity: Entity) : HitResult(hitPos)
}
sealed interface HitType {
    object Horizontal : HitType
    object Vertical : HitType
}

fun raycast(initialPos: Vector2d, level: Level, angle: Double, ignore: Entity? = null, depth: Int = 64): HitResult? {
    val grid = 64

    val dir = direction(angle)

    var mapX = (initialPos.x / grid).toInt()
    var mapY = (initialPos.y / grid).toInt()

    val epsilon = 1e-9
    val deltaDistX = if (abs(dir.x) < epsilon) Double.MAX_VALUE else abs(grid / dir.x)
    val deltaDistY = if (abs(dir.y) < epsilon) Double.MAX_VALUE else abs(grid / dir.y)

    val stepX: Int
    val stepY: Int

    var sideDistX: Double
    var sideDistY: Double

    if (dir.x < 0) {
        stepX = -1
        sideDistX = (initialPos.x - mapX * grid) * (deltaDistX / grid)
    } else {
        stepX = 1
        sideDistX = ((mapX + 1) * grid - initialPos.x) * (deltaDistX / grid)
    }

    if (dir.y < 0) {
        stepY = -1
        sideDistY = (initialPos.y - mapY * grid) * (deltaDistY / grid)
    } else {
        stepY = 1
        sideDistY = ((mapY + 1) * grid - initialPos.y) * (deltaDistY / grid)
    }

    var sideHit: HitType
    var currentDepth = 0

    while (currentDepth < depth) {
        if (sideDistX < sideDistY) {
            sideDistX += deltaDistX
            mapX += stepX
            sideHit = HitType.Vertical
        } else {
            sideDistY += deltaDistY
            mapY += stepY
            sideHit = HitType.Horizontal
        }

        if (mapX !in 0..<level.mapX || mapY !in 0..<level.mapY) {
            break
        }

        val tilePos = level.get(mapX, mapY)
        val tileType = level.getType(tilePos)
        val entities = level.getEntities(tilePos)

        val hitEntity = entities.firstOrNull { it !== ignore }
        if (hitEntity != null) {
            return HitResult.EntityHit(Vector2d(hitEntity.x, hitEntity.y), hitEntity)
        }

        if (tileType !is TileAir) {
            val exactHitDistance = if (sideHit == HitType.Vertical) (sideDistX - deltaDistX) else (sideDistY - deltaDistY)
            val hitX = initialPos.x + dir.x * exactHitDistance
            val hitY = initialPos.y + dir.y * exactHitDistance
            return HitResult.TileHit(Vector2d(hitX, hitY), sideHit)
        }

        currentDepth++
    }

    return null
}