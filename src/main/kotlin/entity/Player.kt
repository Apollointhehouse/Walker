package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.input.PlayerInputHandler
import dev.apollointhehouse.walker.render.Renderer
import dev.apollointhehouse.walker.utils.MathHelper
import org.apache.logging.log4j.kotlin.logger
import org.lwjgl.opengl.GL11

class Player : Entity {
    override var x: Double = 0.0
    override var y: Double = 0.0

    override var xo: Double = 0.0
    override var yo: Double = 0.0

    override var dx: Double = 0.0
    override var dy: Double = 0.0

    override var dxo: Double = 0.0
    override var dyo: Double = 0.0

    private val speed = 1/20.0

    private val input = PlayerInputHandler()

    init {
        Renderer.addDrawable(this)
    }

    override fun tick() {
        xo = x
        yo = y

        dyo = dy
        dxo = dx

        input.tick()

//        logger.info("${InputHandler.forward}, ${InputHandler.strafe}")

        dx += input.strafe * speed
        dy += input.forward * speed

        logger.info("dx: %.2f, dy: %.2f".format(dx, dy))

        x += dx
        y += dy

        dx *= 0.6
        dy *= 0.6
    }

    override fun render(deltaTime: Double) {
        GL11.glColor3f(1.0f, 1.0f, 0.0f)
        GL11.glPointSize(8f)
        GL11.glBegin(GL11.GL_POINTS)

        val lerpX = MathHelper.lerp(xo, x, deltaTime)
        val lerpY = MathHelper.lerp(yo, y, deltaTime)

        GL11.glVertex2d(lerpX, lerpY)
        GL11.glEnd()
    }

    companion object {
        val logger = logger()
    }
}