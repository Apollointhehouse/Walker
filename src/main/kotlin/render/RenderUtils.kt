package dev.apollointhehouse.walker.render

import dev.apollointhehouse.walker.utils.math.AABB2d
import org.lwjgl.opengl.GL11.GL_QUADS

object RenderUtils {
    fun drawBB(bb: AABB2d) = Renderer.draw(GL_QUADS) {
        Renderer.addVertex(bb.min.x(), bb.min.y())
        Renderer.addVertex(bb.max.x(), bb.min.y())
        Renderer.addVertex(bb.max.x(), bb.max.y())
        Renderer.addVertex(bb.min.x(), bb.max.y())
    }
}