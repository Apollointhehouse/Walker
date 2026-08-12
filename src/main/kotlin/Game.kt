package dev.apollointhehouse.walker

import dev.apollointhehouse.walker.entity.Player
import dev.apollointhehouse.walker.render.Renderer
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwTerminate

class Game : Tickable {
    val player = Player()

    fun run() {
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