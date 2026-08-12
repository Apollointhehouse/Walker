package dev.apollointhehouse.walker.render

import org.apache.logging.log4j.kotlin.logger
import org.lwjgl.opengl.GL41
import org.lwjgl.opengl.GLCapabilities
import org.lwjgl.system.MemoryStack

object OpenGLHelper {
    private val LOGGER = logger()

    private val errors: MutableSet<String> = mutableSetOf()
    private var versionPairs: IntArray = intArrayOf(4, 6, 4, 3, 4, 1)
    private var contextCreated: Boolean = false
    private var capabilities: GLCapabilities? = null
    private var version: String = "Unknown"
    private var vendor: String = "Unknown"
    private var renderer: String = "Unknown"
    private var major: Int = 0
    private var minor: Int = 0
    private var ATI_STARTING_FREE_AMOUNT = 0

    fun testCapabilities(capabilities: GLCapabilities) {
        contextCreated = true
        LOGGER.info("")
        LOGGER.info("=== GPU VENDOR INFO ===")
        major = GL41.glGetInteger(33307)
        minor = GL41.glGetInteger(33308)
        LOGGER.info("OpenGL $major.$minor")

        version = GL41.glGetString(7938) ?: "Query Failure"
        vendor = GL41.glGetString(7936) ?: "Query Failure"
        renderer = GL41.glGetString(7937) ?: "Query Failure"

        LOGGER.info(version)
        LOGGER.info(vendor)
        LOGGER.info(renderer)
        LOGGER.info("")
        OpenGLHelper.capabilities = capabilities

        if (!capabilities.OpenGL41) {
            LOGGER.error("Does not meet minimum OpenGL support version!")
        }

        if (capabilities.GL_ATI_meminfo) {
            MemoryStack.stackPush().use { stack ->
                val intBuff = stack.mallocInt(4)
                GL41.glGetIntegerv(34811, intBuff)
                ATI_STARTING_FREE_AMOUNT = intBuff.get(0)
            }
        }
    }

    fun totalVRam(): Long {
        if (capabilities!!.GL_NVX_gpu_memory_info) return GL41.glGetInteger(36935).toLong()
        if (!capabilities!!.GL_ATI_meminfo) return -1L

        return ATI_STARTING_FREE_AMOUNT.toLong()
    }

    fun availableVRam(): Long {
        if (capabilities!!.GL_NVX_gpu_memory_info) return GL41.glGetInteger(36937).toLong()
        if (!capabilities!!.GL_ATI_meminfo) return -1L

        MemoryStack.stackPush().use { stack ->
            val intBuff = stack.mallocInt(4)
            GL41.glGetIntegerv(34811, intBuff)
            return intBuff.get(0).toLong()
        }
    }

    fun checkError(info: String?) {
    }

    fun getErrorDescription(error: Int): String = when (error) {
        1280 -> "Invalid Enum"
        1281 -> "Invalid Value"
        1282 -> "Invalid Operation"
        1286 -> "Invalid Framebuffer Operation"
        1285 -> "Out of Memory"
        1284 -> "Stack Underflow"
        1283 -> "Stack Overflow"
        else -> "Unknown Error"
    }
}