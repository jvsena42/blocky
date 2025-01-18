package com.github.jvsena42.blocky.presentation.screen.home

import androidx.compose.runtime.Stable
import com.github.jvsena42.blocky.domain.model.Block

@Stable
data class HomeUiState(
    val blocks: List<Block> = emptyList(),
    val lastUpdateTime: String = "",
    val isOffline: Boolean = false
)
