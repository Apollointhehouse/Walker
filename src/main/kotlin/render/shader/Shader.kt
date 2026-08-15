package dev.apollointhehouse.walker.render.shader

import dev.apollointhehouse.walker.render.OpenGLHelper
import dev.apollointhehouse.walker.render.shader.providers.ShaderProvider
import org.apache.logging.log4j.kotlin.logger
import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL41

open class Shader {
    protected var program: Int = 0
    protected var vertexShader: Int = 0
    protected var fragmentShader: Int = 0
    var supported: Boolean = true
        private set
    var isEnabled: Boolean = false
        protected set
    protected val uniformLocations: MutableMap<String, Int> = mutableMapOf()
    protected val uniformBlockLocations: MutableMap<String, Int> = mutableMapOf()

    @IgnorableReturnValue
    fun compile(folder: ShaderProvider, name: String): Shader {
        if (!this.supported) return this

        OpenGLHelper.checkError("pre compile shader $name")
        delete()

        val fragmentSource = folder.getShaderSource("$name.fsh")
        val vertexSource = folder.getShaderSource("$name.vsh")

        if (fragmentSource == null) return this
        if (vertexSource == null) return this

        vertexShader = GL41.glCreateShader(35633)
        fragmentShader = GL41.glCreateShader(35632)

        GL41.glShaderSource(vertexShader, vertexSource)
        GL41.glShaderSource(fragmentShader, fragmentSource)
        GL41.glCompileShader(vertexShader)
        GL41.glCompileShader(fragmentShader)

        val fragmentShaderCompileStatus = GL41.glGetShaderi(fragmentShader, 35713)
        val vertexShaderCompileStatus = GL41.glGetShaderi(vertexShader, 35713)

        if (fragmentShaderCompileStatus != 1 || vertexShaderCompileStatus != 1) {
            LOGGER.error("Shader $name compile error")

            if (vertexShaderCompileStatus != 1) {
                LOGGER.error("Vertex Shader Info Log: ${GL41.glGetShaderInfoLog(vertexShader, GL41.glGetShaderi(vertexShader, 35716))}")
            }

            if (fragmentShaderCompileStatus != 1) {
                LOGGER.error("Fragment Shader Info Log: ${GL41.glGetShaderInfoLog(fragmentShader, GL41.glGetShaderi(fragmentShader, 35716))}",)
            }

            delete()
            return this
        }

        program = GL41.glCreateProgram()
        GL41.glAttachShader(program, fragmentShader)
        GL41.glAttachShader(program, vertexShader)
        GL41.glLinkProgram(program)

        val programLinkStatus = GL41.glGetProgrami(program, 35714)

        if (programLinkStatus != 1) {
            LOGGER.error("Program Link Error: ")
            LOGGER.error(GL41.glGetProgramInfoLog(program, GL41.glGetProgrami(program, 35716)))
            delete()
            return this
        }

        GL41.glDeleteShader(vertexShader)
        GL41.glDeleteShader(fragmentShader)

        vertexShader = 0
        fragmentShader = 0
        isEnabled = true

        OpenGLHelper.checkError("compile shader $name")
        return this
    }

    fun getUniform(uniform: String): Int {
        var location = uniformLocations.getOrDefault(uniform, -2)
        if (location == -2) {
            location = GL41.glGetUniformLocation(program, uniform)
            uniformLocations[uniform] = location
        }

        return location
    }

    fun getUniformBlock(uniform: String): Int {
        var location = uniformBlockLocations.getOrDefault(uniform, -2)
        if (location == -2) {
            location = GL41.glGetUniformBlockIndex(program, uniform)
            uniformBlockLocations[uniform] = location
        }

        return location
    }

    fun delete() {
        isEnabled = false
        uniformLocations.clear()
        uniformBlockLocations.clear()
        OpenGLHelper.checkError("pre delete shader")
        if (program != 0) {
            GL41.glDeleteProgram(this.program)
            program = 0
        }

        if (fragmentShader != 0) {
            GL41.glDeleteShader(fragmentShader)
            fragmentShader = 0
        }

        if (vertexShader != 0) {
            GL41.glDeleteShader(vertexShader)
            vertexShader = 0
        }

        OpenGLHelper.checkError("delete shader")
    }

    fun id(): Int {
        return program
    }

    fun bind() {
        GL41.glUseProgram(program)
    }

    fun unbind() {
        GL41.glUseProgram(0)
    }

    fun uniformFloat(name: String, value: Float) {
        val loc = getUniform(name)
        if (loc >= 0) {
            GL41.glUniform1f(loc, value)
        }
    }

    fun uniformFloat(name: String, value: Boolean) {
        uniformFloat(name, if (value) 1.0f else 0.0f)
    }

    fun uniformInt(name: String, value: Int) {
        val loc = getUniform(name)
        if (loc >= 0) {
            GL41.glUniform1i(loc, value)
        }
    }

    fun uniformBool(name: String, value: Boolean) {
        uniformInt(name, if (value) 1 else 0)
    }

    fun uniformMat4f(name: String, matrix4f: Matrix4fc) {
        val loc = getUniform(name)
        if (loc >= 0) {
            GL41.glUniformMatrix4fv(loc, false, matrix4f.get(mat4fBuf))
        }
    }

    fun uniformVec2f(name: String, x: Float, y: Float) {
        val loc = getUniform(name)
        if (loc >= 0) {
            GL41.glUniform2f(loc, x, y)
        }
    }

    fun uniformVec2f(name: String, vector2f: Vector2fc) {
        uniformVec2f(name, vector2f.x(), vector2f.y())
    }

    fun uniformVec3f(name: String, x: Float, y: Float, z: Float) {
        val loc = getUniform(name)
        if (loc >= 0) {
            GL41.glUniform3f(loc, x, y, z)
        }
    }

    fun uniformVec3f(name: String, vector3f: Vector3fc) {
        this.uniformVec3f(name, vector3f.x(), vector3f.y(), vector3f.z())
    }

    fun uniformVec4f(name: String, x: Float, y: Float, z: Float, w: Float) {
        val loc = getUniform(name)
        if (loc >= 0) {
            GL41.glUniform4f(loc, x, y, z, w)
        }
    }

    fun uniformVec4f(name: String, vector4f: Vector4fc) {
        uniformVec4f(name, vector4f.x(), vector4f.y(), vector4f.z(), vector4f.w())
    }

    fun uniformVec2i(name: String, x: Int, y: Int) {
        val loc = getUniform(name)
        OpenGLHelper.checkError("end render world")
        if (loc >= 0) {
            GL41.glUniform2i(loc, x, y)
            OpenGLHelper.checkError("end render world")
        }
    }

    fun uniformVec2i(name: String, vector2i: Vector2ic) {
        uniformVec2i(name, vector2i.x(), vector2i.y())
    }

    fun uniformVec3i(name: String, x: Int, y: Int, z: Int) {
        val loc = getUniform(name)
        if (loc >= 0) {
            GL41.glUniform3i(loc, x, y, z)
        }
    }

    fun uniformVec3i(name: String, vector3i: Vector3ic) {
        this.uniformVec3i(name, vector3i.x(), vector3i.y(), vector3i.z())
    }

    fun uniformVec4i(name: String, x: Int, y: Int, z: Int, w: Int) {
        val loc = getUniform(name)
        if (loc >= 0) {
            GL41.glUniform4i(loc, x, y, z, w)
        }
    }

    fun uniformVec4i(name: String, vector4i: Vector4ic) {
        uniformVec4i(name, vector4i.x(), vector4i.y(), vector4i.z(), vector4i.w())
    }

    fun uniformBlockBinding(name: String, index: Int) {
        val loc = getUniformBlock(name)
        if (loc >= 0) {
            GL41.glUniformBlockBinding(this.program, loc, index)
        }
    }

    companion object {
        protected val LOGGER = logger()
        private val mat4fBuf = BufferUtils.createFloatBuffer(16)

        fun getCompileStatus(shader: Int): Int {
            return GL41.glGetShaderi(shader, 35713)
        }
    }
}