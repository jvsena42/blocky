package com.github.jvsena42.blocky.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subscribe(
    @SerialName("action") val action: String = "want",
    @SerialName("data") val data: List<String> = listOf("blocks")
)