package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.render.Drawable
import org.lwjgl.opengl.GL11

class DrawableLevel : Drawable, Level {
    override val size = 64
    override val mapX get() = map.first().size
    override val mapY get() = map.size

    val map: Array<IntArray> = [
        [1, 1, 1, 1, 1, 1, 1, 1],
        [1, 0, 1, 0, 0, 0, 0, 1],
        [1, 0, 1, 0, 0, 0, 0, 1],
        [1, 0, 1, 0, 0, 0, 0, 1],
        [1, 0, 0, 0, 0, 0, 0, 1],
        [1, 0, 0, 0, 0, 1, 0, 1],
        [1, 0, 0, 0, 0, 0, 0, 1],
        [1, 1, 0, 1, 1, 1, 1, 1],
        [1, 0, 0, 0, 0, 0, 0, 1],
        [1, 1, 1, 1, 1, 1, 1, 1],
    ]

    override fun get(x: Int, y: Int): Int {
        if ((x / 64) !in 0..<mapX || (y / 64) !in 0..<mapY) {
            return 0
        }

        return map[mapY - (y / 64) - 1][x / 64]
    }

    override fun render(deltaTime: Double) {
        val gap = 1

        for (row in map.indices) {
            for (col in map[row].indices) {
                if (map[row][col] == 1) GL11.glColor3f(1f, 1f, 1f) else GL11.glColor3f(0f, 0f, 0f)

                val flippedRow = mapY - 1 - row

                val left   = (col)     * size + gap
                val right  = (col + 1) * size - gap
                val top    = (flippedRow)     * size + gap
                val bottom = (flippedRow + 1) * size - gap

                GL11.glBegin(GL11.GL_QUADS)
                GL11.glVertex2i(left,  top)
                GL11.glVertex2i(left,  bottom)
                GL11.glVertex2i(right, bottom)
                GL11.glVertex2i(right, top)
                GL11.glEnd()
            }
        }
    }
}