package com.github.jvsena42.blocky.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.jvsena42.blocky.data.entities.BlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: BlockEntity)

    @Query("SELECT * FROM blocks ORDER BY timestamp DESC LIMIT 10")
    fun getRecentBlocks(): Flow<List<BlockEntity>>

    @Query("DELETE FROM blocks WHERE timestamp NOT IN (SELECT timestamp FROM blocks ORDER BY timestamp DESC LIMIT 10)")
    suspend fun deleteOldBlocks()
}