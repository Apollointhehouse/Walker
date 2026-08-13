package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.render.Drawable
import org.lwjgl.opengl.GL11

class Level : Drawable {
    val mapSize = 64
    val mapX = 8
    val mapY = 8

    val map: Array<IntArray> = [
        [1, 1, 1, 1, 1, 1, 1, 1],
        [1, 0, 1, 0, 0, 0, 0, 1],
        [1, 0, 1, 0, 0, 0, 0, 1],
        [1, 0, 1, 0, 0, 0, 0, 1],
        [1, 0, 0, 0, 0, 0, 0, 1],
        [1, 0, 0, 0, 0, 1, 0, 1],
        [1, 0, 0, 0, 0, 0, 0, 1],
        [1, 1, 1, 1, 1, 1, 1, 1],
    ]

    fun get(x: Int, y: Int): Int {
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

                val left   = (col)     * mapSize + gap
                val right  = (col + 1) * mapSize - gap
                val top    = (flippedRow)     * mapSize + gap
                val bottom = (flippedRow + 1) * mapSize - gap

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