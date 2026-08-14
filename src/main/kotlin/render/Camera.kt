package dev.apollointhehouse.walker.render

import org.joml.Vector2d
import org.lwjgl.opengl.GL11.*

object Camera {
    fun begin(lerpPos: Vector2d) {
        glPushMatrix()
        glTranslated(Renderer.windowWidth / 2.0 - lerpPos.x(), Renderer.windowHeight / 2.0 - lerpPos.y(), 0.0)
    }

    fun end() {
        glPopMatrix()
    }
}