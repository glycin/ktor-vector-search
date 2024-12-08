package com.glycin.icon

import kotlinx.serialization.Serializable

@Serializable
data class Icon(
    val text: String,
    val image: String,
)