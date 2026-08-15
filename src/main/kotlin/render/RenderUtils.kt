package dev.apollointhehouse.walker.render

import dev.apollointhehouse.walker.utils.math.AABB2d
import org.lwjgl.opengl.GL11.GL_QUADS

object RenderUtils {
    fun drawBB(bb: AABB2d) = Renderer.draw(GL_QUADS) {
        Renderer.addVertex(bb.minX(), bb.minY())
        Renderer.addVertex(bb.maxX(), bb.minY())
        Renderer.addVertex(bb.maxX(), bb.maxY())
        Renderer.addVertex(bb.minX(), bb.maxY())
    }
}