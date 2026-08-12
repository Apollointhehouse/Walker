package dev.apollointhehouse.walker.render.shader.providers

interface ShaderProvider {
    fun getShaderSource(name: String): String?
}