package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.input.PlayerInputHandler
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.render.Camera
import dev.apollointhehouse.walker.render.Renderer
import dev.apollointhehouse.walker.render.texture.Textures
import dev.apollointhehouse.walker.utils.math.*
import org.joml.Vector2d
import org.joml.plus
import org.joml.times
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin

class Player(
    val level: Level,
    val position: Vector2d = Vector2d(300.0, 300.0)
) : Entity {
    val oldPosition = Vector2d(position)
    val velocity = Vector2d()
    val oldVelocity = Vector2d(velocity)

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
        angle = fixAngle(angle)

        val addSpeedX = direction.x * SPEED
        val addSpeedY = direction.y * SPEED
        dx += input.forward * addSpeedX
        dy += input.forward * addSpeedY

        if (level.get((x + dx).toInt(), y.toInt()) == 0 || level.get(x.toInt(), y.toInt()) == 1) x += dx
        if (level.get(x.toInt() , (y + dy).toInt()) == 0 || level.get(x.toInt(), y.toInt()) == 1) y += dy

//        logger.info("dx: %.2f, dy: %.2f".format(dx, dy))
//        logger.info("x: %.2f, y: %.2f".format(x, y))
//        logger.info("angle %.2f".format(angle))

        dx *= FRICTION
        dy *= FRICTION
    }

    private fun drawRays(count: Int, precision: Double, deltaTime: Double) {
        for (r in 0..<count) {
            val lerpAngle = lerpAngle(angleO, angle, deltaTime)
            val rawAngle = lerpAngle - (r - count / 2.0 + 0.5) * precision
            val rayAngle = fixAngle(rawAngle)

            val lerpPos = oldPosition.lerp(position, deltaTime, Vector2d())
            val hitResult = raycast(lerpPos, level, rayAngle)
            (val hitPos, var dist) = hitResult

            when (hitResult) {
                is HitResult.Horizontal -> glColor3d(0.9, 0.0, 0.0)
                is HitResult.Vertical -> glColor3d(0.7, 0.0, 0.0)
            }

            glLineWidth(1.0f)

            Camera.begin(lerpPos)
            Renderer.draw(GL_LINES) {
                Renderer.addVertex(lerpPos)
                Renderer.addVertex(hitPos)
            }
            Camera.end()

            val deltaAngle = deltaAngle(lerpAngle, rayAngle)

            dist *= cos(deltaAngle)

            var lineHeight = (level.size * 320) / dist
            val texYStep = 32.0 / ((level.size * 320) / dist)
            var texYOffset = 0.0

            if (lineHeight > 320.0) {
                texYOffset = (lineHeight - 320) / 2.0
                lineHeight = 320.0
            }

            val viewCenterY = 256.0
            val lineOffset = (viewCenterY - lineHeight).toFloat() / 2.0

            val viewStartX = 512.0
            val colWidth = (1024.0 - viewStartX) / count

            val xLeft = (viewStartX + r * colWidth)
            val xRight = (viewStartX + (r + 1) * colWidth)

            val wallTexture = when (hitResult) {
                is HitResult.Horizontal -> Textures.brick
                is HitResult.Vertical -> Textures.wall
            }

            val texY = texYStep * texYOffset
            val texX = when (hitResult) {
                is HitResult.Horizontal -> hitPos.x % 64 / 2.0
                is HitResult.Vertical -> hitPos.y % 64 / 2.0
            }

            Renderer.draw(GL_POINTS) {
                for (screenX in xLeft.toInt()..<xRight.toInt()) {
                    var currentTexY = texY
                    for (y in 0..<lineHeight.toInt()) {
                        var pixelColor = wallTexture[texX.toInt(), currentTexY.toInt()].toFloat()

                        pixelColor *= when (hitResult) {
                            is HitResult.Horizontal -> 0.9f
                            is HitResult.Vertical -> 0.5f
                        }

                        glColor3f(pixelColor, pixelColor, pixelColor)
                        glPointSize(8f)
                        Renderer.addVertex(screenX.toDouble(), y + lineOffset)

                        currentTexY += texYStep
                    }
                }
            }
        }
    }

    override fun render(deltaTime: Double) {
        val lerpPos = oldPosition.lerp(position, deltaTime, Vector2d())
        val lerpVel = oldVelocity.lerp(velocity, deltaTime, Vector2d())

        Camera.begin(lerpPos)

        glPointSize(8f)
        glColor3f(1.0f, 1.0f, 0.0f)
        Renderer.draw(GL_POINTS) {
            Renderer.addVertex(lerpPos)
        }

        glColor3f(1.0f, 1.0f, 0.0f)
        glLineWidth(2f)
        Renderer.draw(GL_LINES) {
            Renderer.addVertex(lerpPos)
            Renderer.addVertex(lerpPos + lerpVel * SPEED)
        }

        Camera.end()

        val rayCount = (FOV / PRECISION).toInt()
        drawRays(rayCount, Math.toRadians(PRECISION), deltaTime)
    }

    companion object {
        private const val FOV = 80.0
        private const val PRECISION = 0.125

        private const val SPEED = 6.0
        private const val FRICTION = 0.5
    }
}