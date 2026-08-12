package dev.apollointhehouse.walker.render.shader.providers

import org.apache.logging.log4j.kotlin.logger
import java.io.InputStream

class InternalShader : ShaderProvider {
    private val logger = logger()

    override fun getShaderSource(name: String): String? {
        try {
            val stream: InputStream? = javaClass.getResourceAsStream("/assets/${SHADERS_FOLDER}$name")
            if (stream == null) {
                logger.error("Shader ${SHADERS_FOLDER}$name not found!")
                return null
            }

            return stream.reader().readText()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    companion object {
        private const val SHADERS_FOLDER = "shaders/"
    }
}