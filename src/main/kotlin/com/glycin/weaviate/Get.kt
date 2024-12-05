package com.glycin.weaviate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("Get")
    val get: Get
)

@Serializable
data class Get(
    @SerialName("Icon")
    val icon: List<WeaviateIcon>?,
)

@Serializable
data class WeaviateIcon(
    @SerialName("image")
    val image: String,
)