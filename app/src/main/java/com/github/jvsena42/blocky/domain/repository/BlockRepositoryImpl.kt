package com.github.jvsena42.blocky.domain.repository

import com.github.jvsena42.blocky.data.datasource.WebSocketDataSource
import com.github.jvsena42.blocky.db.BlockDao
import com.github.jvsena42.blocky.domain.mapper.toEntity
import com.github.jvsena42.blocky.domain.mapper.toModel
import com.github.jvsena42.blocky.domain.model.Block
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class BlockRepositoryImpl(
    private val webSocketDataSource: WebSocketDataSource,
    private val blockDao: BlockDao
) : BlockRepository {

    override fun getBlocks(): Flow<List<Block>> {
        webSocketDataSource.connectToBlockUpdates().onEach { block ->
            blockDao.insertBlock(block.toEntity())
            blockDao.deleteOldBlocks()
        }

        return blockDao.getRecentBlocks()
            .map { entities ->
                entities.map { it.toModel() }
            }
    }

    override suspend fun disconnect() {
        webSocketDataSource.disconnect()
    }
}
