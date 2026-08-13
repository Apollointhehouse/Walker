package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.input.PlayerInputHandler
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.utils.math.HitResult
import dev.apollointhehouse.walker.utils.math.deltaAngle
import dev.apollointhehouse.walker.utils.math.raycast
import org.apache.logging.log4j.kotlin.logger
import org.joml.Math.lerp
import org.joml.Vector2d
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.min
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

    private fun drawRays(count: Int, precision: Double) {
        for (r in 0..<count) {
            val rawAngle = angle - (r - count / 2.0 + 0.5) * precision
            val rayAngle = ((rawAngle % Math.TAU) + Math.TAU) % Math.TAU

            val hitResult = raycast(position, level, rayAngle)
            (val pos, var dist) = hitResult

            when (hitResult) {
                is HitResult.Horizontal -> glColor3f(0.9f, 0f, 0f)
                is HitResult.Vertical -> glColor3f(0.7f, 0f, 0f)
            }

            glLineWidth(1.0f)
            glBegin(GL_LINES)
            glVertex2d(x, y)
            glVertex2d(pos.x, pos.y)
            glEnd()

            val deltaAngle = deltaAngle(angle, rayAngle)

            dist *= cos(deltaAngle)

            val lineHeight = min((level.mapSize * 320) / dist, 320.0)
            val viewCenterY = 256.0
            val lineOffset = (viewCenterY - lineHeight).toFloat() / 2f

            val screenWidth = 1024.0
            val viewStartX = 530.0
            val colWidth = (screenWidth - viewStartX) / count

            glBegin(GL_QUADS)
            val xLeft = (viewStartX + r * colWidth).toFloat()
            val xRight = (viewStartX + (r + 1) * colWidth).toFloat()
            glVertex2f(xLeft, lineOffset)
            glVertex2f(xRight, lineOffset)
            glVertex2f(xRight, lineHeight.toFloat() + lineOffset)
            glVertex2f(xLeft, lineHeight.toFloat() + lineOffset)
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

        drawRays(240, Math.toRadians(0.25))
    }
}