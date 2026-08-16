package dev.apollointhehouse.walker.render.renderer

import dev.apollointhehouse.walker.input.Input
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.level.tile.TileAir
import dev.apollointhehouse.walker.level.tile.TilePos
import dev.apollointhehouse.walker.render.Drawable
import dev.apollointhehouse.walker.render.camera.Camera
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

class LevelRenderer2D(private val camera: Camera, private val level: Level) : Drawable {
    override fun render(deltaTime: Double) {
        if (!Input.isKeyDown(GLFW.GLFW_KEY_K)) return

        val gap = 1

        camera.apply(deltaTime) {
            for (row in 0..<level.mapY) {
                for (col in 0..<level.mapX) {
                    if (level.getType(TilePos(col, row)) !is TileAir) GL11.glColor3f(1f, 1f, 1f) else GL11.glColor3f(
                        0f,
                        0f,
                        0f
                    )

                    val left = (col) * level.tileSize + gap
                    val right = (col + 1) * level.tileSize - gap
                    val top = (row) * level.tileSize + gap
                    val bottom = (row + 1) * level.tileSize - gap

                    GL11.glBegin(GL11.GL_QUADS)
                    GL11.glVertex2i(left, top)
                    GL11.glVertex2i(left, bottom)
                    GL11.glVertex2i(right, bottom)
                    GL11.glVertex2i(right, top)
                    GL11.glEnd()
                }
            }
        }
    }
}