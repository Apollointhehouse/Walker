package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.Game
import dev.apollointhehouse.walker.input.Input
import dev.apollointhehouse.walker.input.PlayerInputHandler
import dev.apollointhehouse.walker.level.tile.TilePos
import dev.apollointhehouse.walker.render.Renderer
import dev.apollointhehouse.walker.render.texture.Textures
import dev.apollointhehouse.walker.utils.math.*
import org.joml.Vector2d
import org.joml.plus
import org.joml.times
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos

class Player(
    position: Vector2d = Vector2d(300.0, 300.0)
) : Mob(position) {
    override val bb: AABB2d = AABB2d(Vector2d(-16.0, -16.0), Vector2d(16.0, 16.0))

    private val direction: Vector2d
        get() = direction(angle)

    private val input = PlayerInputHandler()

    override fun tick() {
        move()
    }

    private fun move() {
        captureOldState()

        input.tick()

        angle = fixAngle(angle + input.strafe * -Math.toRadians(5.0))

        val addSpeedX = direction.x * SPEED
        val addSpeedY = direction.y * SPEED
        dx += input.forward * addSpeedX
        dy += input.forward * addSpeedY

        x = resolveAxis(x, dx) { testX -> canMoveTo(testX, y) }
        y = resolveAxis(y, dy) { testY -> canMoveTo(x, testY) }

        dx *= FRICTION
        dy *= FRICTION
    }

    private fun drawRays(count: Int, precision: Double, deltaTime: Double) {
        for (r in 0..<count) {
            val lerpAngle = lerpAngle(angleO, angle, deltaTime)
            val rawAngle = lerpAngle - (r - count / 2.0 + 0.5) * precision
            val rayAngle = fixAngle(rawAngle)

            val lerpPos = oldPosition.lerp(position, deltaTime, Vector2d())
            val hitResult = raycast(lerpPos, level, rayAngle, ignore = this) ?: continue

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
                    Renderer.draw(GL_LINES) {
                        Renderer.addVertex(lerpPos)
                        Renderer.addVertex(hitPos)
                    }
                }
            }

            if (Input.isKeyDown(GLFW.GLFW_KEY_K)) continue

            val deltaAngle = deltaAngle(lerpAngle, rayAngle)
            val dist = hitResult.hitPos.distance(lerpPos) * cos(deltaAngle)

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
                    val entityPos = Vector2d(entity.x, entity.y)
                    val dist = lerpPos.distance(entityPos)
                    val angleToEntity = kotlin.math.atan2(entityPos.y - lerpPos.y, entityPos.x - lerpPos.x)

                    var deltaA = fixAngle(rayAngle - angleToEntity)
                    if (deltaA > Math.PI) deltaA -= Math.TAU // wrap to (-PI, PI]

                    val offset = dist * kotlin.math.tan(deltaA)
                    val halfWidth = entity.bb.max.x // assumes symmetric bb like Player's (-16..16)

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

            Renderer.draw(GL_LINES) {
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

                        Renderer.addVertex(screenX.toDouble(), startY + lineOffset)
                        Renderer.addVertex(screenX.toDouble(), startY + countSame + lineOffset)

                        y += countSame
                        currentTexY = tempTexY
                    }
                }
            }
        }
    }

    override fun render(deltaTime: Double) {
        renderDebugBB(deltaTime, Triple(1.0f, 1.0f, 0.0f))

        if (Input.isKeyDown(GLFW.GLFW_KEY_K)) {
            val lerpPos = oldPosition.lerp(position, deltaTime, Vector2d())
            val lerpVel = oldVelocity.lerp(velocity, deltaTime, Vector2d())

            Game.camera.apply(deltaTime) {
                glColor3f(1.0f, 1.0f, 0.0f)
                glLineWidth(2f)
                Renderer.draw(GL_LINES) {
                    Renderer.addVertex(lerpPos)
                    Renderer.addVertex(lerpPos + lerpVel * SPEED)
                }
            }
        }

        val rayCount = (Game.camera.fov / PRECISION).toInt()
        drawRays(rayCount, Math.toRadians(PRECISION), deltaTime)
    }

    companion object {
        private const val PRECISION = 0.125
        private const val SPEED = 6.5
        private const val FRICTION = 0.6
    }
}