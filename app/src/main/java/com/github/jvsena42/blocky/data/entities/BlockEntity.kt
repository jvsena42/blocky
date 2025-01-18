package com.github.jvsena42.blocky.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocks")
data class BlockEntity(
    @PrimaryKey
    val hash: String,
    val height: Long,
    val timestamp: Long,
    val size: Int,
    val weight: Int,
    val txCount: Int
)