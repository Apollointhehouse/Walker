package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.entity.Player
import dev.apollointhehouse.walker.input.Input
import dev.apollointhehouse.walker.render.Camera
import dev.apollointhehouse.walker.render.Drawable
import org.joml.Vector2d
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

class DrawableLevel(val player: Player) : Level by player.level, Drawable {
    override fun render(deltaTime: Double) {
        if (!Input.isKeyDown(GLFW.GLFW_KEY_K)) return

        val gap = 1

        Camera.begin(player.oldPosition.lerp(player.position, deltaTime, Vector2d()))

        for (row in 0..<mapY) {
            for (col in 0..<mapX) {
                if (getRaw(col, row) == 1) GL11.glColor3f(1f, 1f, 1f) else GL11.glColor3f(0f, 0f, 0f)

                val flippedRow = mapY - 1 - row

                val left   = (col)     * tileSize + gap
                val right  = (col + 1) * tileSize - gap
                val top    = (flippedRow)     * tileSize + gap
                val bottom = (flippedRow + 1) * tileSize - gap

                GL11.glBegin(GL11.GL_QUADS)
                GL11.glVertex2i(left,  top)
                GL11.glVertex2i(left,  bottom)
                GL11.glVertex2i(right, bottom)
                GL11.glVertex2i(right, top)
                GL11.glEnd()
            }
        }

        Camera.end()
    }
}