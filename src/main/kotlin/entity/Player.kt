package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.input.PlayerInputHandler
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.utils.math.raycast
import org.apache.logging.log4j.kotlin.logger
import org.joml.Math.lerp
import org.joml.Vector2d
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin

class Player(
    private val level: Level,
    private val position: Vector2d = Vector2d(300.0, 300.0)
) : Entity {
    override var x: Double
        get() = position.x()
        private set(value) { position.set(value, y) }
    override var y: Double
        get() = position.y()
        private set(value) { position.set(x, value) }

    override var xo: Double = x
        private set
    override var yo: Double = y
        private set

    override var dx: Double = 0.0
        private set
    override var dy: Double = 0.0
        private set

    override var dxo: Double = dx
        private set
    override var dyo: Double = dy
        private set

    override var angle: Double = 2 * Math.PI
        private set

    var angleO: Double = angle
        private set

    private val direction: Vector2d
        get() =
            Vector2d(
                cos(angle),
                sin(angle),
            )

    private val speed = 8.0

    private val input = PlayerInputHandler()

    override fun tick() {
        move()
    }

    private fun move() {
        xo = x
        yo = y

        dyo = dy
        dxo = dx

        input.tick()

        angleO = angle
        angle += input.strafe * -Math.toRadians(5.0)

        angle = ((angle % Math.TAU) + Math.TAU) % Math.TAU

        val addSpeedX = direction.x * speed
        val addSpeedY = direction.y * speed
        dx = input.forward * addSpeedX
        dy = input.forward * addSpeedY

//        logger.info("dx: %.2f, dy: %.2f".format(dx, dy))
//        logger.info("x: %.2f, y: %.2f".format(x, y))
        logger.info("angle %.2f".format(angle))

        x += dx
        y += dy

        dx *= 0.6
        dy *= 0.6
    }

    private fun drawRays(count: Int) {
        for (r in 1..count) {
            val rawAngle = angle + (r - count / 2) * Math.toRadians(1.0)
            val rayAngle = ((rawAngle % Math.TAU) + Math.TAU) % Math.TAU

            glColor3f(1f, 0f, 0f)
            glLineWidth(1.0f)
            glBegin(GL_LINES)
            glVertex2d(x, y)

            val (pos) = raycast(position, level, rayAngle)

            glVertex2d(pos.x, pos.y)
            glEnd()
        }
    }

    override fun render(deltaTime: Double) {
        glColor3f(1.0f, 1.0f, 0.0f)
        glPointSize(8f)
        glBegin(GL_POINTS)

        val lerpX = lerp(xo, x, deltaTime)
        val lerpY = lerp(yo, y, deltaTime)

        glVertex2d(lerpX, lerpY)
        glEnd()

        val lerpDX = lerp(dxo, dx, deltaTime)
        val lerpDY = lerp(dyo, dy, deltaTime)

        glColor3f(1.0f, 0.0f, 0.0f)
        glLineWidth(2f)
        glBegin(GL_LINES)
        glVertex2d(x, y)
        glVertex2d(x + lerpDX * speed, y + lerpDY * speed)
        glEnd()

        drawRays(120)
    }
}