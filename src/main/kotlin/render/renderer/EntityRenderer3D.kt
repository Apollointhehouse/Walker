package dev.apollointhehouse.walker.render.renderer

import dev.apollointhehouse.walker.render.RenderUtils
import dev.apollointhehouse.walker.render.camera.Camera
import dev.apollointhehouse.walker.render.texture.Texture
import dev.apollointhehouse.walker.render.texture.Textures
import dev.apollointhehouse.walker.utils.math.HitResult.EntityHit
import org.joml.Vector2dc
import org.joml.minus
import org.lwjgl.opengl.GL11.glColor3d
import kotlin.math.atan2
import kotlin.math.tan

class EntityRenderer3D {
    fun renderColumn(
        hit: EntityHit,
        camera: Camera,
        rayAngle: Double,
        deltaTime: Double,
        screenXRange: IntRange,
        lineHeight: Int,
        lineOffset: Double,
        texYStep: Double,
        texYStart: Double
    ) {
        val cameraPos = camera.getPos(deltaTime)

        val texture = getTexture()
        val texX = getTexX(hit, cameraPos, rayAngle, deltaTime) / 2.0
        val shade = getShade(hit)

        for (screenX in screenXRange) {
            var currentTexY = texYStart
            var y = 0
            while (y < lineHeight) {
                val pixelColor = texture[texX.toInt().coerceIn(0, 31), currentTexY.toInt().coerceIn(0, 31)]
                if (pixelColor == 0) {
                    y++
                    currentTexY += texYStep
                    continue
                }

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

    private fun getTexture(): Texture {
        return Textures.monster
    }

    private fun getTexX(hit: EntityHit, cameraPos: Vector2dc, rayAngle: Double, deltaTime: Double): Double {
        val entity = hit.entity
        val entityPos = entity.getPos(deltaTime)
        val dist = cameraPos.distance(entityPos)

        val dir = entityPos - cameraPos
        val angleToEntity = atan2(dir.y, dir.x)

        var relativeAngle = rayAngle - angleToEntity
        while (relativeAngle > Math.PI) relativeAngle -= 2 * Math.PI
        while (relativeAngle < -Math.PI) relativeAngle += 2 * Math.PI

        val offset = dist * tan(relativeAngle)
        val halfWidth = entity.bb.maxX()

        val normalized = (offset / halfWidth).coerceIn(-1.0, 1.0)
        return (normalized * 0.5 + 0.5) * 64.0
    }

    private fun getShade(hit: EntityHit): Double {
        return 0.9
    }
}
