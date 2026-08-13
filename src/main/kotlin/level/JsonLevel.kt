package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.serialization.JsonLevelModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.logging.log4j.kotlin.logger
import java.io.InputStream

class JsonLevel(val model: JsonLevelModel) : Level {
    override val size get() = model.xSize * model.ySize
    override val mapX get() = model.xSize
    override val mapY get() = model.ySize
    override val tileSize get() = 64

    override fun getRaw(col: Int, row: Int): Int {
        if (col !in 0..<mapX || row !in 0..<mapY) return 0
        return model.data[row * mapX + col]
    }

    override fun get(x: Int, y: Int): Int {
        val tileX = x / tileSize
        val tileY = y / tileSize

        if (tileX !in 0..<mapX || tileY !in 0..<mapY) return 0

        return getRaw(tileX, mapY - 1 - tileY)
    }

    fun get(i: Int): Int {
        if (i >= size) return 0

        val col = i % mapX
        val row = i / mapX
        return getRaw(col, row)
    }

    companion object {
        private val json = Json { prettyPrint = true }

        @OptIn(ExperimentalSerializationApi::class)
        fun load(name: String): JsonLevel {
            val stream: InputStream? = javaClass.getResourceAsStream("/assets/levels/$name")
            if (stream == null) {
                logger.error("Level $name not found!")
                throw IllegalStateException("Failed to load json level $name")
            }

            val model = json.decodeFromStream<JsonLevelModel>(stream)
            return JsonLevel(model)
        }
    }
}