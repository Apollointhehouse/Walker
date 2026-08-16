package dev.apollointhehouse.walker.render.renderer

import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.level.tile.TilePos
import dev.apollointhehouse.walker.render.RenderUtils
import dev.apollointhehouse.walker.render.texture.Texture
import dev.apollointhehouse.walker.render.texture.Textures
import dev.apollointhehouse.walker.utils.math.HitResult.TileHit
import dev.apollointhehouse.walker.utils.math.HitType.Horizontal
import dev.apollointhehouse.walker.utils.math.HitType.Vertical
import org.lwjgl.opengl.GL11.glColor3d

class TileRenderer3D(private val level: Level) {
    fun renderColumn(
        hit: TileHit,
        screenXRange: IntRange,
        lineHeight: Int,
        lineOffset: Double,
        texYStep: Double,
        texYStart: Double
    ) {
        val texture = getTexture(hit)
        val texX = getTexX(hit) / 2.0
        val shade = getShade(hit)

        for (screenX in screenXRange) {
            var currentTexY = texYStart
            var y = 0
            while (y < lineHeight) {
                val pixelColor = texture[texX.toInt().coerceIn(0, 31), currentTexY.toInt().coerceIn(0, 31)]
                val startY = y
                var countSame = 1
                var tempTexY = currentTexY + texYStep
                while (y + countSame < lineHeight &&
                    texture[texX.toInt().coerceIn(0, 31), tempTexY.toInt().coerceIn(0, 31)] == pixelColor
                ) {
                    countSame++
                    tempTexY += texYStep
                }
                when (pixelColor) {
                    0 -> glColor3d(0.0, 0.0, 0.0)
                    1 -> glColor3d(shade, shade, shade)
                    2 -> glColor3d(shade, 0.0, 0.0)
                    3 -> glColor3d(0.0, shade, 0.0)
                    4 -> glColor3d(0.0, 0.0, shade)
                    else -> glColor3d(0.0, 0.0, 0.0)
                }

                RenderUtils.drawLine(
                    screenX.toDouble(),
                    startY + lineOffset,
                    screenX.toDouble(),
                    startY + countSame + lineOffset
                )
                y += countSame
                currentTexY = tempTexY
            }
        }
    }

    private fun getTexture(hit: TileHit): Texture {
        val (x, y) = hit.hitPos
        val tileType = level.getType(TilePos((x / level.tileSize).toInt(), (y / level.tileSize).toInt()))
        return tileType.texture ?: Textures.brick
    }

    private fun getTexX(hit: TileHit): Double = when (hit.hitType) {
        is Horizontal -> ((hit.hitPos.x % 64) + 64) % 64
        is Vertical   -> ((hit.hitPos.y % 64) + 64) % 64
    }

    private fun getShade(hit: TileHit): Double = when (hit.hitType) {
        is Horizontal -> 0.8
        is Vertical   -> 0.6
    }
}
