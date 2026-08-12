package dev.apollointhehouse.walker.entity

import dev.apollointhehouse.walker.Tickable
import dev.apollointhehouse.walker.render.Drawable

interface Entity : Drawable, Tickable {
    val x: Double
    val y: Double

    val xo: Double
    val yo: Double

    val dx: Double
    val dy: Double

    val dxo: Double
    val dyo: Double

    val angle: Double
}