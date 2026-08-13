package dev.apollointhehouse.walker.render

import org.joml.Vector2d
import org.lwjgl.opengl.GL11.*

object Camera {
    private const val VIEW_WIDTH = 512.0
    private const val VIEW_HEIGHT = 512.0

    fun begin(lerpPos: Vector2d) {
        glPushMatrix()
        glTranslated(VIEW_WIDTH / 2.0 - lerpPos.x(), VIEW_HEIGHT / 2.0 - lerpPos.y(), 0.0)
    }

    fun end() {
        glPopMatrix()
    }
}