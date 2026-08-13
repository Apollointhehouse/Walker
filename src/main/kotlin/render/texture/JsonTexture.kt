@file:OptIn(ExperimentalSerializationApi::class)
package dev.apollointhehouse.walker.render.texture

import dev.apollointhehouse.walker.serialization.JsonTextureModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.logging.log4j.kotlin.logger
import java.io.InputStream

class JsonTexture(private val model: JsonTextureModel) : Texture {
    val size get() = model.xSize*model.ySize

    override fun get(x: Int, y: Int): Int {
        val safeX = x.coerceIn(0, model.xSize - 1)
        val safeY = y.coerceIn(0, model.ySize - 1)
        return model.data[(model.ySize - 1 - safeY) * model.xSize + safeX]
    }

    fun get(i: Int): Int {
        val safeI = i.coerceIn(0, model.data.size - 1)
        val x = safeI % model.xSize
        val y = safeI / model.xSize
        return model.data[(model.ySize - 1 - y) * model.xSize + x]
    }

    companion object {
        private val json = Json {
            prettyPrint = true
        }

        fun load(name: String): JsonTexture {
            val stream: InputStream? = javaClass.getResourceAsStream("/assets/textures/$name")
            if (stream == null) {
                logger.error("Texture $name not found!")
                throw IllegalStateException("Texture $name not found!")
            }

            val model = json.decodeFromStream<JsonTextureModel>(stream)

            return JsonTexture(model)
        }
    }
}