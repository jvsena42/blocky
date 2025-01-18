package com.github.jvsena42.blocky.domain.mapper

import com.github.jvsena42.blocky.data.dto.response.BlockDTO
import com.github.jvsena42.blocky.data.entities.BlockEntity
import com.github.jvsena42.blocky.domain.model.Block

fun BlockDTO.toEntity() = BlockEntity(
    hash = id,
    height = height,
    timestamp = timestamp,
    size = size,
    weight = weight,
    txCount = txCount
)

fun BlockEntity.toModel() = Block(
    hash = hash,
    height = height,
    timestamp = timestamp,
    size = size,
    weight = weight,
    txCount = txCount
)

fun BlockDTO.toModel() = Block(
    hash = id,
    height = height,
    timestamp = timestamp,
    size = size,
    weight = weight,
    txCount = txCount
)