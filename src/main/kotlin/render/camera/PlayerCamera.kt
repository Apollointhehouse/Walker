package dev.apollointhehouse.walker.render.camera

import dev.apollointhehouse.walker.entity.Player
import dev.apollointhehouse.walker.render.Renderer
import org.joml.Vector2dc
import org.lwjgl.opengl.GL11.*

class PlayerCamera(private val player: Player) : Camera {
    override val fov: Double = 80.0

    override fun apply(deltaTime: Double, block: () -> Unit) {
        val pos = getPos(deltaTime)

        glPushMatrix()
        glTranslated(Renderer.windowWidth / 2.0 - pos.x(), Renderer.windowHeight / 2.0 - pos.y(), 0.0)

        try {
            block()
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        } finally {
            glPopMatrix()
        }
    }

    override fun getPos(deltaTime: Double): Vector2dc = player.getPos(deltaTime)
    override fun getAngle(deltaTime: Double): Double = player.getAngle(deltaTime)

    override fun tick() {}
}