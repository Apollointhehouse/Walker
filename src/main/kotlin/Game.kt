package dev.apollointhehouse.walker

import dev.apollointhehouse.walker.entity.Monster
import dev.apollointhehouse.walker.entity.Player
import dev.apollointhehouse.walker.level.DrawableLevel
import dev.apollointhehouse.walker.level.JsonLevel
import dev.apollointhehouse.walker.render.PlayerCamera
import dev.apollointhehouse.walker.render.Renderer
import org.joml.Vector2d
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose

object Game : Tickable {
    val camera = PlayerCamera()
    val player = Player()
    val level = DrawableLevel(player, JsonLevel.load("level.json"))

    fun run() {
        player.level = level

        level.addEntity(Monster(Vector2d(player.position), level))

        try {
            Renderer.init()
            loop()
            Renderer.cleanup()
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        } finally {
            // Terminate GLFW and free the error callback
            glfwTerminate()
            glfwSetErrorCallback(null)?.free()
        }
    }

    private fun loop() {
        var lastTime = System.nanoTime()
        val nsPerTick = 1000000000.0 / 20.0
        var delta = 0.0

        // Render loop
        while (!glfwWindowShouldClose(Renderer.window)) {
            val now = System.nanoTime()
            delta += (now - lastTime) / nsPerTick
            lastTime = now

            while (delta >= 1.0) {
                tick()
                delta--
            }

            Renderer.render(delta)
        }
    }

    override fun tick() {
        level.tick()
    }
}