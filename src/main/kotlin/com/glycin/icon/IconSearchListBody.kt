package com.glycin.icon

import kotlinx.serialization.Serializable

@Serializable
data class IconSearchListBody(
    val searchTexts: List<String>,
)