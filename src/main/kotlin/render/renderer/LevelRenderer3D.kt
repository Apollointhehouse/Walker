package dev.apollointhehouse.walker.render.renderer

import dev.apollointhehouse.walker.Game
import dev.apollointhehouse.walker.input.Input
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.level.tile.TilePos
import dev.apollointhehouse.walker.render.Drawable
import dev.apollointhehouse.walker.render.RenderUtils
import dev.apollointhehouse.walker.render.Renderer
import dev.apollointhehouse.walker.render.camera.Camera
import dev.apollointhehouse.walker.render.texture.Textures
import dev.apollointhehouse.walker.utils.math.HitResult.EntityHit
import dev.apollointhehouse.walker.utils.math.HitResult.TileHit
import dev.apollointhehouse.walker.utils.math.HitType.Horizontal
import dev.apollointhehouse.walker.utils.math.HitType.Vertical
import dev.apollointhehouse.walker.utils.math.deltaAngle
import dev.apollointhehouse.walker.utils.math.fixAngle
import dev.apollointhehouse.walker.utils.math.raycast
import org.joml.minus
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.glColor3d
import org.lwjgl.opengl.GL11.glLineWidth
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.tan

class LevelRenderer3D(private val camera: Camera, private val level: Level) : Drawable {
    override fun render(deltaTime: Double) {
        val count = (Game.camera.fov / PRECISION).toInt()

        for (r in 0..<count) {
            val angle = camera.getAngle(deltaTime)
            val rawAngle = angle - (r - count / 2.0 + 0.5) * Math.toRadians(PRECISION)
            val rayAngle = fixAngle(rawAngle)

            val cameraPos = camera.getPos(deltaTime)
            val hitResult = raycast(cameraPos, level, rayAngle) ?: continue

            val hitPos = hitResult.hitPos

            when (hitResult) {
                is TileHit -> when (hitResult.hitType) {
                    is Horizontal -> glColor3d(0.9, 0.0, 0.0)
                    is Vertical   -> glColor3d(0.7, 0.0, 0.0)
                }
                is EntityHit -> glColor3d(0.0, 1.0, 0.0)
            }

            glLineWidth(1.0f)

            if (Input.isKeyDown(GLFW.GLFW_KEY_K)) {
                Game.camera.apply(deltaTime) {
                    RenderUtils.drawLine(cameraPos, hitPos)
                }
            }

            if (Input.isKeyDown(GLFW.GLFW_KEY_K)) continue

            val deltaAngle = deltaAngle(angle, rayAngle)
            val dist = hitResult.hitPos.distance(cameraPos) * cos(deltaAngle)

            var lineHeight = (level.tileSize * Renderer.windowHeight / 2.0 * 1.25) / dist
            val texYStep = 32.0 / ((level.tileSize * Renderer.windowHeight / 2.0 * 1.25) / dist)
            var texYOffset = 0.0

            if (lineHeight > Renderer.windowHeight.toDouble()) {
                texYOffset = (lineHeight - Renderer.windowHeight) / 2.0
                lineHeight = Renderer.windowHeight.toDouble()
            }

            val viewCenterY = Renderer.windowHeight / 2.0
            val lineOffset = (viewCenterY - lineHeight / 2.0)

            val viewStartX = 0.0
            val colWidth = Renderer.windowWidth.toDouble() / count

            val xLeft = (viewStartX + r * colWidth)
            val xRight = (viewStartX + (r + 1) * colWidth)

            val texture = when (hitResult) {
                is TileHit -> {
                    val (x, y) = hitResult.hitPos
                    val tileType = level.getType(TilePos((x / level.tileSize).toInt(), (y / level.tileSize).toInt()))

                    tileType.texture ?: Textures.brick
                }
                is EntityHit -> Textures.monster
            }

            val texYStart = texYStep * texYOffset
            val texX = when (hitResult) {
                is TileHit -> when (hitResult.hitType) {
                    is Horizontal -> ((hitPos.x % 64) + 64) % 64
                    is Vertical   -> ((hitPos.y % 64) + 64) % 64
                }
                is EntityHit -> {
                    val entity = hitResult.entity
                    val entityPos = entity.getPos(deltaTime)
                    val dist = cameraPos.distance(entityPos)

                    val dir = entityPos - cameraPos
                    val angleToEntity = atan2(dir.x, dir.y)

                    val offset = dist * tan(deltaAngle(rayAngle, angleToEntity))
                    val halfWidth = entity.bb.maxX()

                    val normalized = (offset / halfWidth).coerceIn(-1.0, 1.0)
                    (normalized * 0.5 + 0.5) * 64.0
                }
            } / 2.0

            val shade = when (hitResult) {
                is TileHit -> when (hitResult.hitType) {
                    is Horizontal -> 0.8
                    is Vertical   -> 0.6
                }
                is EntityHit -> 0.9
            }

            for (screenX in xLeft.toInt()..<xRight.toInt()) {
                var currentTexY = texYStart
                var y = 0
                while (y < lineHeight.toInt()) {
                    val pixelColor =
                        texture[texX.toInt().coerceIn(0, 31), currentTexY.toInt().coerceIn(0, 31)]
                    val startY = y
                    var countSame = 1
                    var tempTexY = currentTexY + texYStep
                    while (y + countSame < lineHeight.toInt() &&
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

                    RenderUtils.drawLine(screenX.toDouble(), startY + lineOffset, screenX.toDouble(), startY + countSame + lineOffset)
                    y += countSame
                    currentTexY = tempTexY
                }
            }
        }
    }

    companion object {
        private const val PRECISION = 0.125
    }
}
