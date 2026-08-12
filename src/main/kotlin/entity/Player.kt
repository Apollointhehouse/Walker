package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.input.PlayerInputHandler
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.utils.MathHelper
import org.apache.logging.log4j.kotlin.logger
import org.joml.Vector2d
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class Player(private val level: Level) : Entity {
    override var x: Double = 300.0
        private set
    override var y: Double = 300.0
        private set

    override var xo: Double = 0.0
        private set
    override var yo: Double = 0.0
        private set

    override var dx: Double = 0.0
        private set
    override var dy: Double = 0.0
        private set

    override var dxo: Double = 0.0
        private set
    override var dyo: Double = 0.0
        private set

    override var angle: Double = 2 * Math.PI
        private set

    private val direction: Vector2d
        get() =
            Vector2d(
                cos(angle),
                sin(angle),
            )

    private val speed = 8.0

    private val input = PlayerInputHandler()

    private var raycastHitOld: Vector2d? = null

    override fun tick() {
        move()
    }

    private fun move() {
        xo = x
        yo = y

        dyo = dy
        dxo = dx

        input.tick()

        angle += input.strafe * -Math.toRadians(5.0)

        if (angle < 0.0)      angle = Math.TAU
        if (angle > Math.TAU) angle = 0.0

        val addSpeedX = direction.x * speed
        val addSpeedY = direction.y * speed
        dx = input.forward * addSpeedX
        dy = input.forward * addSpeedY

        logger.info("dx: %.2f, dy: %.2f".format(dx, dy))
        logger.info("x: %.2f, y: %.2f".format(x, y))

        x += dx
        y += dy

        dx *= 0.6
        dy *= 0.6
    }

    private fun raycast(): Vector2d {
        var r: Int = 0
        var mapX: Int = 0
        var mapY: Int = 0
        var mapPos: Int = 0
        var dof: Int = 0

        var rayX: Double = 0.0
        var rayY: Double = 0.0
        var rayAngle: Double = 0.0
        var xOffset: Double = 0.0
        var yOffset: Double = 0.0

        rayAngle = angle

        for (r in 0..<1) {
            val aTan = -1.0 / tan(rayAngle)

            if (rayAngle > Math.PI) {
                rayY = (y.toInt() shr 6 shl 6) - 0.0001
                rayX = (y - rayY) * aTan + x
                yOffset -= 64
                xOffset -= yOffset * aTan
            }

            if (rayAngle < Math.PI) {
                rayY = (y.toInt() shr 6 shl 6) + 64.0
                rayX = (y - rayY) * aTan + x
                yOffset += 64
                xOffset -= yOffset * aTan
            }

            if (rayAngle == 0.0 || rayAngle == Math.PI) {
                rayX = x
                rayY = y

                dof = 8
            }

            while (dof < 8) {
                mapX = rayX.toInt() shr 6
                mapY = rayY.toInt() shr 6

                if (mapX < level.mapX && mapY < level.mapY && level.get(mapX, mapY) == 1) {
                    dof = 8
                } else {
                    rayX += xOffset
                    rayY += yOffset
                    dof += 1
                }
            }

        }


        return Vector2d(rayX, rayY)
    }

    override fun render(deltaTime: Double) {
        glColor3f(1.0f, 1.0f, 0.0f)
        glPointSize(8f)
        glBegin(GL_POINTS)

        val lerpX = MathHelper.lerp(xo, x, deltaTime)
        val lerpY = MathHelper.lerp(yo, y, deltaTime)

        glVertex2d(lerpX, lerpY)
        glEnd()

        val lerpDX = MathHelper.lerp(dxo, dx, deltaTime)
        val lerpDY = MathHelper.lerp(dyo, dy, deltaTime)

        glColor3f(1.0f, 0.0f, 0.0f)
        glLineWidth(2f)
        glBegin(GL_LINES)
        glVertex2d(x, y)
        glVertex2d(x + lerpDX * speed, y + lerpDY * speed)
        glEnd()

        val hit = raycast()

        glColor3f(0f, 1f, 0f)
        glLineWidth(1.0f)
        glBegin(GL_LINES)
        glVertex2d(x, y)


        val old = raycastHitOld

        if (old != null) {
            val lerpHitX = MathHelper.lerp(old.x, hit.x, deltaTime)
            val lerpHitY = MathHelper.lerp(old.y, hit.y, deltaTime)

            glVertex2d(lerpHitX, lerpHitY)
        } else {
            glVertex2d(hit.x, hit.y)
        }

        raycastHitOld = hit

        glEnd()
    }
}