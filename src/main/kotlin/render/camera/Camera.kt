package dev.apollointhehouse.walker.render.camera

import dev.apollointhehouse.walker.Tickable
import org.joml.Vector2dc

interface Camera : Tickable {
    val fov: Double

    fun apply(deltaTime: Double, block: () -> Unit)

    fun getPosition(deltaTime: Double): Vector2dc
}