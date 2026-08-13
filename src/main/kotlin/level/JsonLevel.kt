package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.render.Drawable
import dev.apollointhehouse.walker.serialization.JsonLevelModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.logging.log4j.kotlin.logger
import org.lwjgl.opengl.GL11
import java.io.InputStream

class JsonLevel(private val model: JsonLevelModel) : Drawable, Level {
    override val size get() = model.xSize * model.ySize
    override val mapX get() = model.xSize
    override val mapY get() = model.ySize

    private fun rawTile(col: Int, row: Int): Int {
        if (col !in 0..<mapX || row !in 0..<mapY) return 0
        return model.data[row * mapX + col]
    }

    override fun get(x: Int, y: Int): Int {
        val tileX = x / TILE_SIZE
        val tileY = y / TILE_SIZE

        if (tileX !in 0..<mapX || tileY !in 0..<mapY) return 0

        return rawTile(tileX, mapY - 1 - tileY)
    }

    fun get(i: Int): Int {
        if (i >= size) return 0

        val col = i % mapX
        val row = i / mapX
        return rawTile(col, row)
    }

    override fun render(deltaTime: Double) {
        val gap = 1

        for (row in 0..<mapY) {
            for (col in 0..<mapX) {
                if (rawTile(col, row) == 1) GL11.glColor3f(1f, 1f, 1f) else GL11.glColor3f(0f, 0f, 0f)

                val flippedRow = mapY - 1 - row

                val left   = (col)     * TILE_SIZE + gap
                val right  = (col + 1) * TILE_SIZE - gap
                val top    = (flippedRow)     * TILE_SIZE + gap
                val bottom = (flippedRow + 1) * TILE_SIZE - gap

                GL11.glBegin(GL11.GL_QUADS)
                GL11.glVertex2i(left,  top)
                GL11.glVertex2i(left,  bottom)
                GL11.glVertex2i(right, bottom)
                GL11.glVertex2i(right, top)
                GL11.glEnd()
            }
        }
    }

    companion object {
        private const val TILE_SIZE = 64

        private val json = Json { prettyPrint = true }

        @OptIn(ExperimentalSerializationApi::class)
        fun load(name: String): JsonLevel? {
            try {
                val stream: InputStream? = javaClass.getResourceAsStream("/assets/levels/$name")
                if (stream == null) {
                    logger.error("Level $name not found!")
                    return null
                }

                val model = json.decodeFromStream<JsonLevelModel>(stream)
                return JsonLevel(model)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}