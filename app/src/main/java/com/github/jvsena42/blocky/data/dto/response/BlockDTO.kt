package com.github.jvsena42.blocky.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockDTO(
    val height: Long,
    val hash: String,
    val timestamp: Long,
    val size: Int,
    val weight: Int,
    @SerialName("tx_count") val tcCount: Int
)