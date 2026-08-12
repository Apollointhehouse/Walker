package dev.apollointhehouse.walker.input

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback

object Input {
    private val keys = BooleanArray(65536)

    fun initialize(windowId: Long) {
        GLFW.glfwSetKeyCallback(windowId, object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                if (key >= 0 && key < keys.size) {
                    if (action == GLFW.GLFW_PRESS) {
                        keys[key] = true
                    } else if (action == GLFW.GLFW_RELEASE) {
                        keys[key] = false
                    }
                }
            }
        })
    }

    fun isKeyDown(keyCode: Int): Boolean {
        return keys[keyCode]
    }
}
