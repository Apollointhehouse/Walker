package dev.apollointhehouse.walker

import dev.apollointhehouse.walker.entity.Player
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.render.Renderer
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwTerminate

class Game : Tickable {
    val level = Level()
    val player = Player(level)

    fun run() {
        Renderer.addDrawable(level)
        Renderer.addDrawable(player)

        try {
            val renderer = Renderer(this)

            renderer.init()
            renderer.loop()
        } finally {
            // Terminate GLFW and free the error callback
            glfwTerminate()
            glfwSetErrorCallback(null)?.free()
        }
    }

    override fun tick() {
        player.tick()
    }
}