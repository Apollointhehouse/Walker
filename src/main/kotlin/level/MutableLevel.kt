package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.entity.Entity

interface MutableLevel : Level {
    fun addEntity(entity: Entity)
}