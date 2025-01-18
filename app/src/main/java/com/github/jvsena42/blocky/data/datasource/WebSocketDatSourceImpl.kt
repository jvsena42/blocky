package com.github.jvsena42.blocky.data.datasource

import android.util.Log
import com.github.jvsena42.blocky.BuildConfig
import com.github.jvsena42.blocky.data.dto.request.Subscribe
import com.github.jvsena42.blocky.data.dto.response.BlockDTO
import com.github.jvsena42.blocky.data.dto.response.BlocksResponse
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
import java.io.IOException
import java.nio.channels.UnresolvedAddressException


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

                            if (text.contains(""""blocks":""")) {
                                try {
                                    val blocksJson = text
                                    val blocksResponse = json.decodeFromString<BlocksResponse>(blocksJson)
                                    blocksResponse.blocks.forEach { block ->
                                        trySend(block)
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Failed to decode JSON: $text", e)
                                }
                            } else {
                                Log.w(TAG, "JSON does not contain 'blocks' key: $text")
                            }
                        }

                        is Frame.Close -> {
                            Log.d(TAG, "connectToBlockUpdates: close")
                            close()
                        }

                        else -> {
                            Log.d(TAG, "connectToBlockUpdates: else $frame")
                        }
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to connect to WebSocket: device offline", e)
            webSocketSession?.close(reason = CloseReason(CloseReason.Codes.TRY_AGAIN_LATER, "Closing"))
        }  catch (e: UnresolvedAddressException) {
            Log.e(TAG, "Failed to connect to WebSocket: Unresolved address", e)
            webSocketSession?.close(reason = CloseReason(CloseReason.Codes.CLOSED_ABNORMALLY, "Closing"))
        }  catch (e: Exception) {
            Log.e(TAG, "connectToBlockUpdates: catch", e)
            webSocketSession?.close(reason = CloseReason(CloseReason.Codes.CLOSED_ABNORMALLY, "Closing"))
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