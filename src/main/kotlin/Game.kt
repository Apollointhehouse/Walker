package dev.apollointhehouse.walker

import dev.apollointhehouse.walker.entity.Player
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.render.Renderer
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose

class Game : Tickable {
    val level = Level()
    val player = Player(level)

    fun run() {
        Renderer.addDrawable(level)
        Renderer.addDrawable(player)

        try {
            Renderer.init()
            loop()
            Renderer.cleanup()
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
        player.tick()
    }
}