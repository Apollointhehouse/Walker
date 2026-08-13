package dev.apollointhehouse.walker.serialization

import kotlinx.serialization.Serializable

@Serializable
class JsonTextureModel(
    val xSize: Int,
    val ySize: Int,
    val data: IntArray,
)