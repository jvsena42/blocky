package com.github.jvsena42.blocky.data.datasource

import com.github.jvsena42.blocky.data.dto.response.BlockDTO
import kotlinx.coroutines.flow.Flow

interface WebSocketDataSource {
    fun  connectToBlockUpdates(): Flow<BlockDTO>
    suspend fun disconnect()
}