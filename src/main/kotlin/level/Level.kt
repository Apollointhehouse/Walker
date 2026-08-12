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

    override fun render(deltaTime: Double) {
        for (row in map.indices) {
            for (col in map[row].indices) {
                if (map[row][col] == 1) GL11.glColor3f(1f, 1f, 1f) else GL11.glColor3f(0f, 0f, 0f)

                val flippedRow = mapY - 1 - row

                GL11.glBegin(GL11.GL_QUADS)
                GL11.glVertex2i((col)     * mapSize, (flippedRow)     * mapSize)
                GL11.glVertex2i((col)     * mapSize, (flippedRow + 1) * mapSize)
                GL11.glVertex2i((col + 1) * mapSize, (flippedRow + 1) * mapSize)
                GL11.glVertex2i((col + 1) * mapSize, (flippedRow)     * mapSize)
                GL11.glEnd()
            }
        }
    }
}