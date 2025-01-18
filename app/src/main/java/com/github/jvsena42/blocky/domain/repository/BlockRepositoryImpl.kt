package com.github.jvsena42.blocky.domain.repository

import android.util.Log
import com.github.jvsena42.blocky.data.datasource.WebSocketDataSource
import com.github.jvsena42.blocky.db.BlockDao
import com.github.jvsena42.blocky.domain.mapper.toEntity
import com.github.jvsena42.blocky.domain.mapper.toModel
import com.github.jvsena42.blocky.domain.model.Block
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class BlockRepositoryImpl(
    private val webSocketDataSource: WebSocketDataSource,
    private val blockDao: BlockDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BlockRepository {

    override fun getBlocks(): Flow<List<Block>> {
        Log.d(TAG, "getBlocks: ")

        CoroutineScope(ioDispatcher).launch {
            Log.d(TAG, "init")
            webSocketDataSource.connectToBlockUpdates().onEach { block ->
                Log.d(TAG, "Received block: $block")
                blockDao.insertBlock(block.toEntity())
                blockDao.deleteOldBlocks()
            }.collect()
        }
        
        return blockDao.getRecentBlocks()
            .map { entities ->
                Log.d(TAG, "getBlocks: return")
                entities.map { it.toModel() }
            }
    }

    override suspend fun disconnect() {
        webSocketDataSource.disconnect()
    }

    companion object {
        private const val TAG = "BlockRepositoryImpl"
    }
}
