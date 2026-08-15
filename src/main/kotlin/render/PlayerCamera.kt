package dev.apollointhehouse.walker.render

import dev.apollointhehouse.walker.Game
import org.joml.Vector2d
import org.joml.Vector2dc
import org.lwjgl.opengl.GL11.*

class PlayerCamera : Camera {
    override val fov: Double = 60.0

    private fun cameraPos(deltaTime: Double): Vector2d =
        Game.player.oldPosition.lerp(Game.player.position, deltaTime, Vector2d())

    override fun apply(deltaTime: Double, block: () -> Unit) {
        val lerpPos = cameraPos(deltaTime)

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
        Game.player.oldPosition.lerp(Game.player.position, deltaTime, Vector2d())

    override fun tick() {}
}