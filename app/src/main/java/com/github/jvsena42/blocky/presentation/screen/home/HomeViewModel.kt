package com.github.jvsena42.blocky.presentation.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jvsena42.blocky.domain.repository.BlockRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeViewModel(
    private val blockRepository: BlockRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            blockRepository.getBlocks().collect { blocks ->
                Log.d(TAG, "collectedBlocks: ${blocks.take(10)}")
                _uiState.value = _uiState.value.copy(
                    blocks = blocks,
                    lastUpdateTime = getformattedTime()
                )
            }
        }
    }

    fun onAction(action: HomeActions) {
        Log.d(TAG, "onAction: $action")
        when (action) {
            HomeActions.OnClickSearch -> {} //TODO IMPLEMENT
        }
    }

    private fun getformattedTime(): String {
        return LocalDateTime.now().toString().format(DATE_TIME_PATTERN)
    }

    companion object {
        private const val TAG = "HomeViewModel"
        private const val DATE_TIME_PATTERN = "dd/MMM/yyyy HH:mm:ss"
    }
}