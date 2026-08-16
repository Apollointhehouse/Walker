package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.Game
import dev.apollointhehouse.walker.input.Input
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.level.tile.TileAir
import dev.apollointhehouse.walker.render.RenderUtils
import dev.apollointhehouse.walker.utils.math.AABB2d
import dev.apollointhehouse.walker.utils.math.AABB2dc
import dev.apollointhehouse.walker.utils.math.HitResult
import dev.apollointhehouse.walker.utils.math.fixAngle
import dev.apollointhehouse.walker.utils.math.raycast
import org.joml.Vector2d
import org.lwjgl.glfw.GLFW

abstract class Mob(
    override val pos: Vector2d
) : Entity {
    override val oldPos = Vector2d(pos)
    override val velocity = Vector2d()
    override val oldVelocity = Vector2d(velocity)

    override var level: Level? = null

    abstract override val bb: AABB2dc

    override var x: Double
        get() = pos.x()
        protected set(value) { pos.set(value, y) }
    override var y: Double
        get() = pos.y()
        protected set(value) { pos.set(x, value) }

    override var xo: Double
        get() = oldPos.x()
        protected set(value) { oldPos.set(value, yo) }
    override var yo: Double
        get() = oldPos.y()
        protected set(value) { oldPos.set(xo, value) }

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

    override var angleO: Double = angle
        protected set

    protected fun captureOldState() {
        xo = x
        yo = y
        dxo = dx
        dyo = dy
        angleO = angle
    }

    protected fun resolveAxis(
        current: Double,
        delta: Double,
        testBB: AABB2d,
        canMove: (Double, AABB2d) -> Boolean
    ): Double {
        if (delta == 0.0) return current
        if (canMove(current + delta, testBB)) return current + delta

        var lo = 0.0
        var hi = delta
        repeat(25) {
            val mid = (lo + hi) / 2.0
            if (canMove(current + mid, testBB)) lo = mid else hi = mid
        }
        return current + lo
    }

    protected fun canMoveTo(testX: Double, testY: Double, testBB: AABB2d): Boolean {
        val level = level ?: return false

        val mapWidth = level.mapX * level.tileSize
        val mapHeight = level.mapY * level.tileSize

        bb.translate(testX, testY, testBB)

        if (testBB.minX < 0.0 || testBB.maxY >= mapWidth.toDouble()) return false
        if (testBB.minY < 0.0 || testBB.maxY >= mapHeight.toDouble()) return false

        val minTileX = testBB.minX.toInt() / level.tileSize
        val maxTileX = testBB.maxX.toInt() / level.tileSize
        val minTileY = testBB.minY.toInt() / level.tileSize
        val maxTileY = testBB.maxY.toInt() / level.tileSize

        for (tileX in minTileX..maxTileX) {
            for (tileY in minTileY..maxTileY) {
                if (level.getType(level.get(tileX, tileY)) !is TileAir) return false
            }
        }

        testBB.setMin(0.0, 0.0)
        testBB.setMax(0.0, 0.0)

        return true
    }

    protected fun moveWithCollision(friction: Double) {
        x = resolveAxis(x, dx, AABB2d()) { testX, testBB -> canMoveTo(testX, y, testBB) }
        y = resolveAxis(y, dy, AABB2d()) { testY, testBB -> canMoveTo(x, testY, testBB) }

        dx *= friction
        dy *= friction
    }

    protected fun renderDebugBB(deltaTime: Double, color: Triple<Float, Float, Float>) {
        if (!Input.isKeyDown(GLFW.GLFW_KEY_K)) return

        Game.camera.apply(deltaTime) {
            org.lwjgl.opengl.GL11.glColor3f(color.first, color.second, color.third)
            RenderUtils.drawBB(bb + getPos(deltaTime))
        }
    }

    protected fun castVisionCone(
        fov: Double,
        rayCount: Int,
        depth: Int = 64
    ): List<Pair<Entity, Double>> {
        val level = level ?: return emptyList()
        if (rayCount <= 0) return emptyList()

        val hits = mutableListOf<Pair<Entity, Double>>()

        for (r in 0..<rayCount) {
            val t = if (rayCount == 1) 0.5 else r.toDouble() / (rayCount - 1)
            val rayAngle = fixAngle(angle - fov / 2.0 + fov * t)

            val hit = raycast(pos, level, rayAngle, depth = depth)
            if (hit is HitResult.EntityHit) {
                hits.add(hit.entity to hit.hitPos.distance(pos))
            }
        }

        return hits.sortedBy { it.second }
    }
}