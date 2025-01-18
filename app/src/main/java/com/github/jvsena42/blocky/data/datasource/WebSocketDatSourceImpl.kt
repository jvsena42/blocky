package com.github.jvsena42.blocky.data.datasource

import android.util.Log
import com.github.jvsena42.blocky.BuildConfig
import com.github.jvsena42.blocky.data.dto.request.Subscribe
import com.github.jvsena42.blocky.data.dto.response.BlockDTO
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer


class WebSocketDatSourceImpl(
    private val client: HttpClient,
    private val json: Json
) : WebSocketDataSource {

    private var webSocketSession: DefaultWebSocketSession? = null

    override fun connectToBlockUpdates(): Flow<BlockDTO> = callbackFlow {
        Log.d(TAG, "connectToBlockUpdates: ")
        try {
            client.webSocket(BuildConfig.MEMPOOL_WS_URL) {
                webSocketSession = this

                val subscribeMessage = Subscribe()
                Log.d(TAG, "connectToBlockUpdates: data class: $subscribeMessage")
                val jsonMessage = json.encodeToString<Subscribe>(
                    Subscribe.serializer(),
                    subscribeMessage
                )
                Log.d(TAG, "connectToBlockUpdates: send $jsonMessage")
                send(Frame.Text(jsonMessage))

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            Log.d(TAG, "connectToBlockUpdates: $text")
                            if (text.contains(""""block":""")) {
                                val block = json.decodeFromString<BlockDTO>(
                                    text.substringAfter(""""block":""").trim()
                                )
                                trySend(block)
                            }
                        }

                        else -> {
                            Log.d(TAG, "connectToBlockUpdates: else $frame")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "connectToBlockUpdates: catch", e)
            close(e)
        }

        awaitClose {
            webSocketSession = null
        }
    }

    override suspend fun disconnect() {
        webSocketSession?.close(reason = CloseReason(CloseReason.Codes.NORMAL, "Closing"))
        webSocketSession = null
    }

    companion object {
        const val TAG = "WebSocketDataSource"
    }
}