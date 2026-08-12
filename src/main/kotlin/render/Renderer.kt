package dev.apollointhehouse.walker.render

import dev.apollointhehouse.walker.Game
import dev.apollointhehouse.walker.input.Input
import dev.apollointhehouse.walker.render.shader.Shaders
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL41.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Renderer(val game: Game) {
    private var window: Long = 0
    private var vao: Int = 0
    private var vbo: Int = 0

    fun init() {
        GLFWErrorCallback.createPrint(System.err).set()

        check(glfwInit()) { "Unable to initialize GLFW" }

        // Configure GLFW
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        // Create the window
        window = glfwCreateWindow(1024, 512, "Hello World!", NULL, NULL)
        if (window == NULL) throw RuntimeException("Failed to create the GLFW window")

        // Setup a key callback
        Input.initialize(window)

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
        GL.createCapabilities()

        // Enable v-sync
        glfwSwapInterval(1)

        Shaders.init()

        vao = glGenVertexArrays()
        glBindVertexArray(vao)

        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        val vertices = floatArrayOf(
            100f, 100f,
            400f, 100f,
            400f, 400f,
            100f, 400f
        )
        val buffer: FloatBuffer = BufferUtils.createFloatBuffer(vertices.size)
        buffer.put(vertices)
        buffer.flip()

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        // Make the window visible
        glfwShowWindow(window)
    }

    fun loop() {
        var lastTime = System.nanoTime()
        val nsPerTick = 1000000000.0 / 20.0
        var delta = 0.0

        // Render loop
        while (!glfwWindowShouldClose(window)) {
            val now = System.nanoTime()
            delta += (now - lastTime) / nsPerTick
            lastTime = now

            while (delta >= 1.0) {
                game.tick()
                delta--
            }

            render(delta)
        }

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)
    }

    private fun render(deltaTime: Double) {
        glClearColor(0.3f, 0.3f, 0.3f, 0.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

//        val orthoMatrix = Matrix4f().ortho(0f, 1024f, 512f, 0f, -1.0f, 1.0f)
//        Shaders.BASE.bind()
//        Shaders.BASE.uniformMat4f("uProjection", orthoMatrix)
//        Shaders.BASE.uniformVec4f("uColor", Vector4f(1f, 0f, 0f, 0f))

        for (drawable in drawables) {
            drawable.render(deltaTime)
        }

        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4)

        glfwSwapBuffers(window)

        // Poll for window events
        glfwPollEvents()
    }

    companion object {
        private val drawables = mutableListOf<Drawable>()

        fun addDrawable(drawable: Drawable): Boolean = drawables.add(drawable)
    }
}