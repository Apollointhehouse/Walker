package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.Game
import dev.apollointhehouse.walker.input.Input
import dev.apollointhehouse.walker.input.PlayerInputHandler
import dev.apollointhehouse.walker.render.Renderer
import dev.apollointhehouse.walker.utils.math.AABB2d
import dev.apollointhehouse.walker.utils.math.AABB2dc
import dev.apollointhehouse.walker.utils.math.direction
import dev.apollointhehouse.walker.utils.math.fixAngle
import org.joml.Vector2d
import org.joml.plus
import org.joml.times
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*

class Player(
    position: Vector2d = Vector2d(300.0, 300.0)
) : Mob(position) {
    override val bb: AABB2dc = AABB2d(Vector2d(-16.0, -16.0), Vector2d(16.0, 16.0))

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

        val testBB = AABB2d()
        x = resolveAxis(x, dx, testBB) { testX, testBB -> canMoveTo(testX, y, testBB) }
        y = resolveAxis(y, dy, testBB) { testY, testBB -> canMoveTo(x, testY, testBB) }

        dx *= FRICTION
        dy *= FRICTION
    }

    override fun render(deltaTime: Double) {
        renderDebugBB(deltaTime, Triple(1.0f, 1.0f, 0.0f))

        if (Input.isKeyDown(GLFW.GLFW_KEY_K)) {
            val lerpPos = getPos(deltaTime)
            val lerpVel = getVelocity(deltaTime)

            Game.camera.apply(deltaTime) {
                glColor3f(1.0f, 1.0f, 0.0f)
                glLineWidth(2f)
                Renderer.draw(GL_LINES) {
                    Renderer.addVertex(lerpPos)
                    Renderer.addVertex(lerpPos + lerpVel * SPEED)
                }
            }
        }
    }

    companion object {
        private const val SPEED = 6.5
        private const val FRICTION = 0.6
    }
}