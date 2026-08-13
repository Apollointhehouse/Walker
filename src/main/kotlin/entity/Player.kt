package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.input.PlayerInputHandler
import dev.apollointhehouse.walker.level.Level
import org.apache.logging.log4j.kotlin.logger
import org.joml.Math.lerp
import org.joml.Vector2d
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

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

    private var raycastHit: Vector2d? = null
    private var raycastHitOld: Vector2d? = null

    override fun tick() {
        move()

        raycastHitOld = raycastHit
        raycastHit = raycast(angle)
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

    private fun raycast(angle: Double): Vector2d {
        var mapX = 0
        var mapY = 0
        var dof = 0

        var rayHX = 0.0
        var rayHY = 0.0

        var rayVX = 0.0
        var rayVY = 0.0

        var xOffset = 0.0
        var yOffset = 0.0

        var hDist = Double.POSITIVE_INFINITY
        var vDist = Double.POSITIVE_INFINITY

        for (r in 0..<1) {
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

            hDist = position.distance(rayHX, rayHY)


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

            vDist = position.distance(rayVX, rayVY)

        }


        return if (hDist < vDist) {
            Vector2d(rayHX, rayHY)
        } else {
            Vector2d(rayVX, rayVY)
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

        glColor3f(0f, 1f, 0f)
        glLineWidth(1.0f)
        glBegin(GL_LINES)
        glVertex2d(x, y)

        val lerpHit = raycastHit?.let { raycastHitOld?.lerp(it, deltaTime, Vector2d()) } ?: position

        glVertex2d(lerpHit.x, lerpHit.y)
        glEnd()
    }
}