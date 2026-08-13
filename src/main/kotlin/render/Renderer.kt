package dev.apollointhehouse.walker.render

import dev.apollointhehouse.walker.input.Input
import org.joml.Vector2d
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL41.glBindVertexArray
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.IntBuffer

object Renderer {
    private val drawables = mutableListOf<Drawable>()

    var window: Long = 0
        private set

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

        stackPush().use { stack ->
            val fbWidth = stack.mallocInt(1)
            val fbHeight = stack.mallocInt(1)
            glfwGetFramebufferSize(window, fbWidth, fbHeight)
            glViewport(0, 0, fbWidth.get(0), fbHeight.get(0))
        }

        glfwSetFramebufferSizeCallback(window) { _, width, height ->
            glViewport(0, 0, width, height)
        }

        // Enable v-sync
        glfwSwapInterval(1)

        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glClearColor(0.3f, 0.3f, 0.3f, 0.0f)
        glOrtho(0.0, 1024.0, 0.0, 512.0, -1.0, 1.0)
        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()

        // Make the window visible
        glfwShowWindow(window)
    }

    fun cleanup() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)
    }

    fun render(deltaTime: Double) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)


        for (drawable in drawables) {
            drawable.render(deltaTime)
        }

        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4)

        glfwSwapBuffers(window)

        // Poll for window events
        glfwPollEvents()
    }

    fun addDrawable(drawable: Drawable): Boolean = drawables.add(drawable)
    fun addVertex(vert: Vector2d) = glVertex2d(vert.x, vert.y)
    fun addVertex(x: Double, y: Double) = glVertex2d(x, y)

    fun begin(glMode: Int) = glBegin(glMode)
    fun end() = glEnd()

    inline fun draw(glMode: Int, block: () -> Unit) {
        begin(glMode)
        block()
        end()
    }
}