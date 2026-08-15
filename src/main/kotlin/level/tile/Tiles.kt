package dev.apollointhehouse.walker.level.tile

object Tiles {
    private val tileMap = mutableMapOf<Int, TileType>()

    init {
        register(0, TileAir)
        register(1, TileWall)
    }

    fun register(id: Int, tileType: TileType) {
        tileMap[id] = tileType
    }

    fun get(id: Int): TileType? {
        return tileMap[id]
    }
}