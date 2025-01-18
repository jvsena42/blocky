package com.github.jvsena42.blocky.presentation.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jvsena42.blocky.domain.repository.BlockRepository
import com.github.jvsena42.blocky.domain.util.NetworkStatusTracker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel(
    private val blockRepository: BlockRepository,
    private val networkStatusTracker: NetworkStatusTracker,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        Log.d(TAG, "init")
        viewModelScope.launch(ioDispatcher) {
            blockRepository.getBlocks().collect { blocks ->
                Log.d(TAG, "collectedBlocks: ${blocks.take(10)}")
                _uiState.value = _uiState.value.copy(
                    blocks = blocks,
                    lastUpdateTime = getformattedTime()
                )
            }

            networkStatusTracker.isOnline.collect { isOnline ->
                Log.d(TAG, "isOnline: $isOnline")
                _uiState.value = _uiState.value.copy(isOffline = !isOnline)
            }
        }
        viewModelScope.launch(ioDispatcher) {
            networkStatusTracker.isOnline.collect { isOnline ->
                Log.d(TAG, "isOnline: $isOnline")
                _uiState.value = _uiState.value.copy(isOffline = !isOnline)
            }
        }
    }

    fun onAction(action: HomeActions) {
        Log.d(TAG, "onAction: $action")
        when (action) {
            HomeActions.OnClickSearch -> {} //TODO IMPLEMENT
        }
    }

    private fun getformattedTime(): String { //TODO SAVE THE DATE IN PREFERENCES AT EVERY SUCCESS REQUEST
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(ioDispatcher) {
            blockRepository.disconnect()
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
        private const val DATE_TIME_PATTERN = "dd/MMM/yyyy HH:mm:ss"
    }
}