package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.input.PlayerInputHandler
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.render.Renderer
import dev.apollointhehouse.walker.utils.math.*
import org.apache.logging.log4j.kotlin.logger
import org.joml.Vector2d
import org.joml.plus
import org.joml.times
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class Player(
    private val level: Level,
    private val position: Vector2d = Vector2d(300.0, 300.0)
) : Entity {
    private val oldPosition = Vector2d(position)
    private val velocity = Vector2d()
    private val oldVelocity = Vector2d(velocity)

    override var x: Double
        get() = position.x()
        private set(value) { position.set(value, y) }
    override var y: Double
        get() = position.y()
        private set(value) { position.set(x, value) }

    override var xo: Double
        get() = oldPosition.x()
        private set(value) { oldPosition.set(value, yo) }
    override var yo: Double
        get() = oldPosition.y()
        private set(value) { oldPosition.set(xo, value) }

    override var dx: Double
        get() = velocity.x()
        private set(value) { velocity.set(value, dy) }
    override var dy: Double
        get() = velocity.y()
        private set(value) { velocity.set(dx, value) }

    override var dxo: Double
        get() = oldVelocity.x()
        private set(value) { oldVelocity.set(value, dyo) }
    override var dyo: Double
        get() = oldVelocity.y()
        private set(value) { oldVelocity.set(dxo, value) }

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

        val addSpeedX = direction.x * SPEED
        val addSpeedY = direction.y * SPEED
        dx += input.forward * addSpeedX
        dy += input.forward * addSpeedY

        if (level.get((x + dx).toInt(), y.toInt()) == 0) x += dx
        if (level.get(x.toInt() , (y + dy).toInt()) == 0) y += dy

//        logger.info("dx: %.2f, dy: %.2f".format(dx, dy))
        logger.info("x: %.2f, y: %.2f".format(x, y))
//        logger.info("angle %.2f".format(angle))

//        x += dx
//        y += dy

        dx *= FRICTION
        dy *= FRICTION
    }

    private fun drawRays(count: Int, precision: Double, deltaTime: Double) {
        for (r in 0..<count) {
            val lerpAngle = lerpAngle(angleO, angle, deltaTime)
            val rawAngle = lerpAngle - (r - count / 2.0 + 0.5) * precision
            val rayAngle = ((rawAngle % Math.TAU) + Math.TAU) % Math.TAU

            val hitResult = raycast(oldPosition.lerp(position, deltaTime, Vector2d()), level, rayAngle)
            (val pos, var dist) = hitResult

            val colorMult = angleRange(rayAngle, 0.9, 1.1)

            when (hitResult) {
                is HitResult.Horizontal -> glColor3d(colorMult * 0.9, 0.0, 0.0)
                is HitResult.Vertical -> glColor3d(colorMult * 0.7, 0.0, 0.0)
            }

            glLineWidth(1.0f)

            Renderer.draw(GL_LINES) {
                glVertex2d(x, y)
                glVertex2d(pos.x, pos.y)
            }

            val deltaAngle = deltaAngle(lerpAngle, rayAngle)

            dist *= cos(deltaAngle)

            val lineHeight = min((level.mapSize * 320) / dist, 320.0)
            val viewCenterY = 256.0
            val lineOffset = (viewCenterY - lineHeight).toFloat() / 2f

            val screenWidth = 1024.0
            val viewStartX = 530.0
            val colWidth = (screenWidth - viewStartX) / count

            val xLeft = (viewStartX + r * colWidth).toFloat()
            val xRight = (viewStartX + (r + 1) * colWidth).toFloat()

            Renderer.draw(GL_QUADS) {
                glVertex2f(xLeft, lineOffset)
                glVertex2f(xRight, lineOffset)
                glVertex2f(xRight, lineHeight.toFloat() + lineOffset)
                glVertex2f(xLeft, lineHeight.toFloat() + lineOffset)
            }
        }
    }

    override fun render(deltaTime: Double) {
        glPointSize(8f)
        glColor3f(1.0f, 1.0f, 0.0f)
        val lerpPos = oldPosition.lerp(position, deltaTime, Vector2d())

        Renderer.draw(GL_POINTS) {
            Renderer.addVertex(lerpPos)
        }

        val lerpVel = oldVelocity.lerp(velocity, deltaTime, Vector2d())

        glColor3f(1.0f, 0.0f, 0.0f)
        glLineWidth(2f)

        Renderer.draw(GL_LINES) {
            Renderer.addVertex(position)
            Renderer.addVertex(position + lerpVel * SPEED)
        }

        drawRays((FOV / PRECISION).toInt(), Math.toRadians(PRECISION), deltaTime)
    }

    companion object {
        private const val FOV = 80.0
        private const val PRECISION = 0.125

        private const val SPEED = 6.0
        private const val FRICTION = 0.5
    }
}