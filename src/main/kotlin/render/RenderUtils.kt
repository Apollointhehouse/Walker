package dev.apollointhehouse.walker.render

import dev.apollointhehouse.walker.utils.math.AABB2dc
import org.joml.Vector2dc
import org.lwjgl.opengl.GL11.GL_LINES
import org.lwjgl.opengl.GL11.GL_QUADS

object RenderUtils {
    fun drawBB(bb: AABB2dc) = Renderer.draw(GL_QUADS) {
        Renderer.addVertex(bb.minX(), bb.minY())
        Renderer.addVertex(bb.maxX(), bb.minY())
        Renderer.addVertex(bb.maxX(), bb.maxY())
        Renderer.addVertex(bb.minX(), bb.maxY())
    }

    fun drawLine(a: Vector2dc, b: Vector2dc) = drawLine(a.x(), a.y(), b.x(), b.y())

    fun drawLine(
        x0: Double, y0: Double,
        x1: Double, y1: Double
    ) = Renderer.draw(GL_LINES) {
        Renderer.addVertex(x0, y0)
        Renderer.addVertex(x1, y1)
    }
}