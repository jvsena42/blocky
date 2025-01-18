package com.github.jvsena42.blocky.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.jvsena42.blocky.data.entities.BlockEntity

@Database(
    entities = [BlockEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BlockDatabase : RoomDatabase() {
    abstract val blockDao: BlockDao
}