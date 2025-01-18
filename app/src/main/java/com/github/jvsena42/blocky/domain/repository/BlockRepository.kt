package com.github.jvsena42.blocky.domain.repository

import com.github.jvsena42.blocky.domain.model.Block
import kotlinx.coroutines.flow.Flow

interface BlockRepository {
    fun getBlocks(): Flow<List<Block>>
    suspend fun disconnect()
}