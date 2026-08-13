package dev.apollointhehouse.walker.render.texture

interface Texture {
    operator fun get(x: Int, y: Int): Int
}