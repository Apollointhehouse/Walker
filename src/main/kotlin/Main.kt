package dev.apollointhehouse.walker

fun main() {
    System.setProperty("org.lwjgl.util.Debug", "true")
    System.setProperty("org.lwjgl.util.DebugLoader", "true")
    Game.run()
}