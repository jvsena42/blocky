package com.github.jvsena42.blocky.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlocksResponse(
    @SerialName("blocks") val blocks: List<BlockDTO>
)

@Serializable
data class BlockDTO(
    @SerialName("id") val id: String,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("height") val height: Long,
    @SerialName("version") val version: Int,
    @SerialName("bits") val bits: Int,
    @SerialName("nonce") val nonce: Long,
    @SerialName("difficulty") val difficulty: Double,
    @SerialName("merkle_root") val merkleRoot: String,
    @SerialName("tx_count") val txCount: Int,
    @SerialName("size") val size: Int,
    @SerialName("weight") val weight: Int,
    @SerialName("previousblockhash") val previousBlockHash: String,
    @SerialName("extras") val extras: Extras
)

@Serializable
data class Extras(
    @SerialName("coinbaseRaw") val coinbaseRaw: String,
    @SerialName("medianFee") val medianFee: Double,
    @SerialName("feeRange") val feeRange: List<Double>,
    @SerialName("reward") val reward: Long,
    @SerialName("totalFees") val totalFees: Long,
    @SerialName("avgFee") val avgFee: Double,
    @SerialName("avgFeeRate") val avgFeeRate: Double,
    @SerialName("pool") val pool: Pool
)

@Serializable
data class Pool(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("slug") val slug: String
)