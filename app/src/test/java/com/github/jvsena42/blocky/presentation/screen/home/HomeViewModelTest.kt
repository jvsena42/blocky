package com.github.jvsena42.blocky.presentation.screen.home


import android.util.Log
import app.cash.turbine.test
import com.github.jvsena42.blocky.domain.model.Block
import com.github.jvsena42.blocky.domain.repository.BlockRepository
import com.github.jvsena42.blocky.domain.util.NetworkStatusTracker
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
    private val blockRepository: BlockRepository = mockk()
    private val networkStatusTracker: NetworkStatusTracker = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val networkStatusFlow = MutableStateFlow(true)
    private val blocksFlow = MutableStateFlow<List<Block>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.v(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0

        coEvery { blockRepository.getBlocks() } returns blocksFlow
        every { networkStatusTracker.isOnline } returns networkStatusFlow

        viewModel = HomeViewModel(
            blockRepository = blockRepository,
            networkStatusTracker = networkStatusTracker,
            ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(emptyList<Block>(), initialState.blocks)
            assertFalse(initialState.isOffline)
            assertNotNull(initialState.lastUpdateTime)
        }
    }

    @Test
    fun `updates state when new blocks are received`() = runTest {
        // Given
        val testBlocks = listOf(
            Block(height = 1L, hash = "hash1", timestamp = 123L, size = 100, weight = 400, txCount = 10),
            Block(height = 2L, hash = "hash2", timestamp = 124L, size = 200, weight = 500, txCount = 20)
        )

        // When & Then
        viewModel.uiState.test {
            // Initial state
            assertEquals(emptyList<Block>(), awaitItem().blocks)

            // Update blocks
            blocksFlow.value = testBlocks
            advanceUntilIdle()

            // Verify updated state
            val updatedState = awaitItem()
            assertEquals(testBlocks, updatedState.blocks)
            assertNotNull(updatedState.lastUpdateTime)
        }
    }

    @Test
    fun `updates offline status when network status changes`() = runTest {
        viewModel.uiState.test {
            // Initial state (online)
            assertFalse(awaitItem().isOffline)

            // When network becomes offline
            networkStatusFlow.value = false
            advanceUntilIdle()

            // Then state should reflect offline status
            assertTrue(awaitItem().isOffline)

            // When network becomes online again
            networkStatusFlow.value = true
            advanceUntilIdle()

            // Then state should reflect online status
            assertFalse(awaitItem().isOffline)
        }
    }

    @Test
    fun `maintains last update time when network status changes`() = runTest {
        viewModel.uiState.test {
            // Get initial state
            val initialTime = awaitItem().lastUpdateTime

            // When network status changes
            networkStatusFlow.value = false
            advanceUntilIdle()

            // Then lastUpdateTime should not change
            val offlineState = awaitItem()
            assertEquals(initialTime, offlineState.lastUpdateTime)
        }
    }
}