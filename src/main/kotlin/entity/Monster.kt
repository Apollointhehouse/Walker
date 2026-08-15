package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.utils.math.AABB2d
import dev.apollointhehouse.walker.utils.math.fixAngle
import org.joml.Vector2d
import org.joml.minus
import kotlin.math.atan2
import kotlin.random.Random

class Monster(position: Vector2d = Vector2d(300.0, 300.0), override var level: Level) : Mob(position) {

    override val bb: AABB2d = AABB2d(Vector2d(-16.0, -16.0), Vector2d(16.0, 16.0))

    private var turnCooldown = 0

    override fun tick() {
        captureOldState()

        val seenPlayer = castVisionCone(FOV, RAY_COUNT, SIGHT_DEPTH)
            .firstOrNull { it.first is Player }
            ?.first as? Player

        if (seenPlayer != null) {
            chase(seenPlayer)
        } else {
            wander()
        }

        moveWithCollision(FRICTION)
    }

    private fun chase(player: Player) {
        val toPlayer = player.pos - pos
        val dist = toPlayer.length()
        if (dist < 1e-6) return

        toPlayer.normalize()
        angle = fixAngle(atan2(toPlayer.y(), toPlayer.x()))

        dx += toPlayer.x() * CHASE_SPEED
        dy += toPlayer.y() * CHASE_SPEED

        turnCooldown = 0
    }

    private fun wander() {
        if (turnCooldown > 0) {
            turnCooldown--
            return
        }

        val turnAmount = Random.nextDouble(-1.0, 1.0) * MAX_TURN
        angle = fixAngle(angle + turnAmount)
        turnCooldown = Random.nextInt(TURN_COOLDOWN_MIN, TURN_COOLDOWN_MAX)
    }

    override fun render(deltaTime: Double) {
        renderDebugBB(deltaTime, Triple(0.0f, 0.0f, 1.0f))
    }

    companion object {
        private const val CHASE_SPEED = 8.0
        private const val FRICTION = 0.2

        private val FOV = Math.toRadians(360.0)
        private val precision = Math.toRadians(5.0)
        private val RAY_COUNT = (FOV / precision).toInt()
        private const val SIGHT_DEPTH = 20

        private val MAX_TURN = Math.toRadians(30.0)
        private const val TURN_COOLDOWN_MIN = 5
        private const val TURN_COOLDOWN_MAX = 20
    }
}