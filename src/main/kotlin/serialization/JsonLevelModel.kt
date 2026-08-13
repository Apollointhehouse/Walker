package dev.apollointhehouse.walker.serialization

import kotlinx.serialization.Serializable

@Serializable
class JsonLevelModel(
    val xSize: Int,
    val ySize: Int,
    val data: IntArray,
)