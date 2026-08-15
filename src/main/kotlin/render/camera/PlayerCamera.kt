package dev.apollointhehouse.walker.render.camera

import dev.apollointhehouse.walker.entity.Player
import dev.apollointhehouse.walker.render.Renderer
import org.joml.Vector2dc
import org.lwjgl.opengl.GL11.*

class PlayerCamera(private val player: Player) : Camera {
    override val fov: Double = 80.0

    override fun apply(deltaTime: Double, block: () -> Unit) {
        val lerpPos = getPosition(deltaTime)

        glPushMatrix()
        glTranslated(Renderer.windowWidth / 2.0 - lerpPos.x(), Renderer.windowHeight / 2.0 - lerpPos.y(), 0.0)

        try {
            block()
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        } finally {
            glPopMatrix()
        }
    }

    override fun getPosition(deltaTime: Double): Vector2dc =
        player.getPos(deltaTime)

    override fun tick() {}
}