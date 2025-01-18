package com.github.jvsena42.blocky.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
object WebSocketMessageDTO {
    @Serializable
    data class Subscribe(
        @SerialName("action") val action: String = "want",
        @SerialName("data") val data: List<String>
    ) {
        companion object {
            fun subscribeToBlocks() = Subscribe(data = listOf("blocks"))
            fun subscribeToTransactions() = Subscribe(data = listOf("transactions"))
            fun subscribeToAll() = Subscribe(data = listOf("blocks", "transactions"))
        }
    }
}