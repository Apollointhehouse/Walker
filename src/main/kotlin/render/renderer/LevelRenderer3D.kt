package dev.apollointhehouse.walker.render.renderer

import dev.apollointhehouse.walker.input.Input
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.render.Drawable
import dev.apollointhehouse.walker.render.RenderUtils
import dev.apollointhehouse.walker.render.Renderer
import dev.apollointhehouse.walker.render.camera.Camera
import dev.apollointhehouse.walker.utils.math.HitResult.EntityHit
import dev.apollointhehouse.walker.utils.math.HitResult.TileHit
import dev.apollointhehouse.walker.utils.math.deltaAngle
import dev.apollointhehouse.walker.utils.math.fixAngle
import dev.apollointhehouse.walker.utils.math.raycast
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.glColor3d
import org.lwjgl.opengl.GL11.glLineWidth
import kotlin.math.cos

class LevelRenderer3D(private val camera: Camera, private val level: Level) : Drawable {
    private val tileRenderer = TileRenderer3D(level)
    private val entityRenderer = EntityRenderer3D()

    override fun render(deltaTime: Double) {
        val count = (camera.fov / PRECISION).toInt()

        for (r in 0..<count) {
            val angle = camera.getAngle(deltaTime)
            val rawAngle = angle - (r - count / 2.0 + 0.5) * Math.toRadians(PRECISION)
            val rayAngle = fixAngle(rawAngle)

            val cameraPos = camera.getPos(deltaTime)
            val hitResult = raycast(cameraPos, level, rayAngle) ?: continue

            val hitPos = hitResult.hitPos

            glLineWidth(1.0f)

            if (Input.isKeyDown(GLFW.GLFW_KEY_K)) {
                when (hitResult) {
                    is TileHit -> when (hitResult.hitType) {
                        is Horizontal -> glColor3d(0.9, 0.0, 0.0)
                        is Vertical   -> glColor3d(0.7, 0.0, 0.0)
                    }
                    is EntityHit -> glColor3d(0.0, 1.0, 0.0)
                }

                camera.apply(deltaTime) {
                    RenderUtils.drawLine(cameraPos, hitPos)
                }
            }

            if (Input.isKeyDown(GLFW.GLFW_KEY_K)) continue

            val deltaAngle = deltaAngle(angle, rayAngle)
            val dist = if (hitResult is EntityHit) {
                val entityPos = hitResult.entity.getPos(deltaTime)
                cameraPos.distance(entityPos) * cos(deltaAngle)
            } else {
                hitResult.hitPos.distance(cameraPos) * cos(deltaAngle)
            }

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

            val texYStart = texYStep * texYOffset

            when (hitResult) {
                is TileHit -> tileRenderer.renderColumn(
                    hitResult,
                    xLeft.toInt()..<xRight.toInt(),
                    lineHeight.toInt(),
                    lineOffset,
                    texYStep,
                    texYStart
                )
                is EntityHit -> entityRenderer.renderColumn(
                    hitResult,
                    camera,
                    rayAngle,
                    deltaTime,
                    xLeft.toInt()..<xRight.toInt(),
                    lineHeight.toInt(),
                    lineOffset,
                    texYStep,
                    texYStart
                )
            }
        }
    }

    companion object {
        private const val PRECISION = 0.125
    }
}
