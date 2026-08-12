package dev.apollointhehouse.walker.render.shader

import dev.apollointhehouse.walker.render.shader.providers.InternalShader
import dev.apollointhehouse.walker.render.shader.providers.ShaderProvider
import org.apache.logging.log4j.kotlin.logger

object Shaders {
    private val logger = logger()
    private val ALL_SHADERS: MutableList<Shader> = mutableListOf()
    private val SHADER_MAP: MutableMap<String, Shader> = mutableMapOf()
    private val PROVIDER_INTERNAL: ShaderProvider = InternalShader()

    val BASE: Shader = register("base", Shader())

    private fun <T : Shader> register(id: String, shader: T): T {
        if (!ALL_SHADERS.contains(shader)) {
            ALL_SHADERS.add(shader)
        }

        SHADER_MAP[id] = shader
        return shader
    }

    fun init() {
        for ((key, value) in SHADER_MAP) {
            try {
                value.compile(PROVIDER_INTERNAL, key)
            } catch (e: Exception) {
                logger.error("", e)
            }
        }
    }

    fun reload() {
        for ((key, value) in SHADER_MAP) {
            try {
                value.delete()
                value.compile(PROVIDER_INTERNAL, key)
            } catch (e: Exception) {
                logger.error("", e)
            }
        }
    }
}
