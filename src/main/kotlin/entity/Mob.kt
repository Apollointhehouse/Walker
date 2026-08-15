package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.Game
import dev.apollointhehouse.walker.input.Input
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.level.tile.TileAir
import dev.apollointhehouse.walker.render.RenderUtils
import dev.apollointhehouse.walker.utils.math.AABB2d
import dev.apollointhehouse.walker.utils.math.HitResult
import dev.apollointhehouse.walker.utils.math.fixAngle
import dev.apollointhehouse.walker.utils.math.raycast
import org.joml.Vector2d
import org.lwjgl.glfw.GLFW

abstract class Mob(
    override val position: Vector2d
) : Entity {
    val oldPosition = Vector2d(position)
    val velocity = Vector2d()
    val oldVelocity = Vector2d(velocity)

    override lateinit var level: Level

    abstract override val bb: AABB2d

    override var x: Double
        get() = position.x()
        protected set(value) { position.set(value, y) }
    override var y: Double
        get() = position.y()
        protected set(value) { position.set(x, value) }

    override var xo: Double
        get() = oldPosition.x()
        protected set(value) { oldPosition.set(value, yo) }
    override var yo: Double
        get() = oldPosition.y()
        protected set(value) { oldPosition.set(xo, value) }

    override var dx: Double
        get() = velocity.x()
        protected set(value) { velocity.set(value, dy) }
    override var dy: Double
        get() = velocity.y()
        protected set(value) { velocity.set(dx, value) }

    override var dxo: Double
        get() = oldVelocity.x()
        protected set(value) { oldVelocity.set(value, dyo) }
    override var dyo: Double
        get() = oldVelocity.y()
        protected set(value) { oldVelocity.set(dxo, value) }

    override var angle: Double = 2 * Math.PI
        protected set

    var angleO: Double = angle
        protected set

    protected fun captureOldState() {
        xo = x
        yo = y
        dxo = dx
        dyo = dy
        angleO = angle
    }

    protected fun resolveAxis(current: Double, delta: Double, canMove: (Double) -> Boolean): Double {
        if (delta == 0.0) return current
        if (canMove(current + delta)) return current + delta

        var lo = 0.0
        var hi = delta
        repeat(25) {
            val mid = (lo + hi) / 2.0
            if (canMove(current + mid)) lo = mid else hi = mid
        }
        return current + lo
    }

    protected fun canMoveTo(testX: Double, testY: Double): Boolean {
        val mapWidth = level.mapX * level.tileSize
        val mapHeight = level.mapY * level.tileSize

        val testBB = bb + Vector2d(testX, testY)

        if (testBB.min.x() < 0.0 || testBB.max.x() >= mapWidth.toDouble()) return false
        if (testBB.min.y() < 0.0 || testBB.max.y() >= mapHeight.toDouble()) return false

        val minTileX = testBB.min.x().toInt() / level.tileSize
        val maxTileX = testBB.max.x().toInt() / level.tileSize
        val minTileY = testBB.min.y().toInt() / level.tileSize
        val maxTileY = testBB.max.y().toInt() / level.tileSize

        for (tileX in minTileX..maxTileX) {
            for (tileY in minTileY..maxTileY) {
                if (level.getType(level.get(tileX, tileY)) !is TileAir) return false
            }
        }

        return true
    }

    protected fun moveWithCollision(friction: Double) {
        x = resolveAxis(x, dx) { testX -> canMoveTo(testX, y) }
        y = resolveAxis(y, dy) { testY -> canMoveTo(x, testY) }

        dx *= friction
        dy *= friction
    }

    protected fun renderDebugBB(deltaTime: Double, color: Triple<Float, Float, Float>) {
        if (!Input.isKeyDown(GLFW.GLFW_KEY_K)) return

        val lerpPos = oldPosition.lerp(position, deltaTime, Vector2d())

        Game.camera.apply(deltaTime) {
            org.lwjgl.opengl.GL11.glColor3f(color.first, color.second, color.third)
            val adjBB = bb + lerpPos
            RenderUtils.drawBB(adjBB)
        }
    }

    protected fun castVisionCone(
        fov: Double,
        rayCount: Int,
        depth: Int = 64
    ): List<Pair<Entity, Double>> {
        if (rayCount <= 0) return emptyList()

        val hits = mutableListOf<Pair<Entity, Double>>()

        for (r in 0..<rayCount) {
            val t = if (rayCount == 1) 0.5 else r.toDouble() / (rayCount - 1)
            val rayAngle = fixAngle(angle - fov / 2.0 + fov * t)

            val hit = raycast(position, level, rayAngle, ignore = this, depth = depth)
            if (hit is HitResult.EntityHit) {
                hits.add(hit.entity to hit.hitPos.distance(position))
            }
        }

        return hits.sortedBy { it.second }
    }
}