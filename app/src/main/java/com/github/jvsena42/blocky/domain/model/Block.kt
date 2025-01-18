package com.github.jvsena42.blocky.domain.model

data class Block(
    val height: Long,
    val hash: String,
    val timestamp: Long,
    val size: Int,
    val weight: Int,
    val txCount: Int
)
