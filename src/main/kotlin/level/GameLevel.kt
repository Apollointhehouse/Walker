package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.Tickable

class GameLevel(level: MutableLevel) : MutableLevel by level, Tickable {
    override fun tick() {
        for (entity in entities) {
            entity.tick()
        }
    }
}