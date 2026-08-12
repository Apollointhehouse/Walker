package dev.apollointhehouse

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.IntBuffer

class Game {
    // The window handle
    private var window: Long = 0

    fun run() {
        try {
            init()
            loop()

            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window)
            glfwDestroyWindow(window)
        } finally {
            // Terminate GLFW and free the error callback
            glfwTerminate()
            glfwSetErrorCallback(null)?.free()
        }
    }

    private fun init() {
        GLFWErrorCallback.createPrint(System.err).set()

        check(glfwInit()) { "Unable to initialize GLFW" }

        // Configure GLFW
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL)
        if (window == NULL) throw RuntimeException("Failed to create the GLFW window")

        // Setup a key callback
        glfwSetKeyCallback(window) { window, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true)
        }

        stackPush().use { stack ->
            val pWidth: IntBuffer = stack.mallocInt(1) // int*
            val pHeight: IntBuffer = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode: GLFWVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            )
        }
        // Make the OpenGL context current
        glfwMakeContextCurrent(window)

        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window)
    }

    private fun loop() {
        GL.createCapabilities()

        glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

        // Render loop
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            glfwSwapBuffers(window)

            // Poll for window events
            glfwPollEvents()
        }
    }
}