package dev.apollointhehouse.walker.input

import dev.apollointhehouse.walker.Tickable
import org.lwjgl.glfw.GLFW.*
import java.lang.Math.clamp
import kotlin.math.hypot

class PlayerInputHandler : Tickable {
    var forward: Double = 0.0
        private set
    var strafe: Double = 0.0
        private set

    override fun tick() {
        forward = 0.0
        strafe = 0.0

        if (Input.isKeyDown(GLFW_KEY_W)) forward++
        if (Input.isKeyDown(GLFW_KEY_A)) strafe--
        if (Input.isKeyDown(GLFW_KEY_S)) forward--
        if (Input.isKeyDown(GLFW_KEY_D)) strafe++

        forward = clamp(forward, -1.0, 1.0)
        strafe = clamp(strafe, -1.0, 1.0)

        val length = hypot(forward, strafe)

        if (length == 0.0) return

        forward /= length
        strafe /= length
    }
}