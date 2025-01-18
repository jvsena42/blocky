package com.github.jvsena42.blocky.domain.repository

import android.util.Log
import app.cash.turbine.test
import com.github.jvsena42.blocky.data.datasource.WebSocketDataSource
import com.github.jvsena42.blocky.data.dto.response.BlockDTO
import com.github.jvsena42.blocky.data.dto.response.Extras
import com.github.jvsena42.blocky.data.dto.response.Pool
import com.github.jvsena42.blocky.db.BlockDao
import com.github.jvsena42.blocky.domain.mapper.toEntity
import com.github.jvsena42.blocky.domain.model.Block
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BlockRepositoryImplTest {
    private lateinit var repository: BlockRepositoryImpl
    private val webSocketDataSource: WebSocketDataSource = mockk()
    private val blockDao: BlockDao = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.v(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0

        repository = BlockRepositoryImpl(
            webSocketDataSource = webSocketDataSource,
            blockDao = blockDao,
            ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getBlocks starts websocket connection and stores received blocks`() = runTest {

        val blockDTO = BlockDTO(
            id = "hash1",
            timestamp = 123456789L,
            height = 1L,
            version = 1,
            bits = 1,
            nonce = 1L,
            difficulty = 1.0,
            merkleRoot = "merkleRoot",
            txCount = 100,
            size = 1000,
            weight = 4000,
            previousBlockHash = "previousBlockHash",
            extras = Extras(
                coinbaseRaw = "coinbaseRaw",
                medianFee = 1.0,
                feeRange = listOf(1.0),
                reward = 1L,
                totalFees = 1L,
                avgFee = 1.0,
                avgFeeRate = 1.0,
                pool = Pool(
                    id = 1,
                    name = "poolName",
                    slug = "poolSlug"
                )
            )
        )

        val expectedBlock = Block(
            height = 1L,
            hash = "hash1",
            timestamp = 123456789L,
            size = 1000,
            weight = 4000,
            txCount = 100
        )
        val blockEntity = blockDTO.toEntity()

        coEvery { webSocketDataSource.connectToBlockUpdates() } returns flowOf(blockDTO)
        coEvery { blockDao.insertBlock(any()) } just runs
        coEvery { blockDao.deleteOldBlocks() } just runs
        coEvery { blockDao.getRecentBlocks() } returns flowOf(listOf(blockEntity))

        val flow = repository.getBlocks()

        advanceUntilIdle()

        flow.test {
            val emittedBlocks = awaitItem()
            assertEquals(listOf(expectedBlock), emittedBlocks)
            awaitComplete()
        }

        coVerify {
            webSocketDataSource.connectToBlockUpdates()
            blockDao.insertBlock(blockEntity)
            blockDao.deleteOldBlocks()
            blockDao.getRecentBlocks()
        }
    }

    @Test
    fun `getBlocks returns empty list when database is empty`() = runTest {

        coEvery { webSocketDataSource.connectToBlockUpdates() } returns flowOf()
        coEvery { blockDao.getRecentBlocks() } returns flowOf(emptyList())


        repository.getBlocks().test {
            val emittedBlocks = awaitItem()
            assertEquals(emptyList<Block>(), emittedBlocks)
            awaitComplete()
        }
    }

    @Test
    fun `disconnect calls websocket disconnect`() = runTest {

        coEvery { webSocketDataSource.disconnect() } just runs


        repository.disconnect()


        coVerify { webSocketDataSource.disconnect() }
    }

    @Test
    fun `getBlocks handles multiple block updates`() = runTest {

        val blockDTO1 = BlockDTO(
            id = "hash1",
            timestamp = 123456789L,
            height = 1L,
            version = 1,
            bits = 1,
            nonce = 1L,
            difficulty = 1.0,
            merkleRoot = "merkleRoot",
            txCount = 100,
            size = 1000,
            weight = 4000,
            previousBlockHash = "previousBlockHash",
            extras = Extras(
                coinbaseRaw = "coinbaseRaw",
                medianFee = 1.0,
                feeRange = listOf(1.0),
                reward = 1L,
                totalFees = 1L,
                avgFee = 1.0,
                avgFeeRate = 1.0,
                pool = Pool(
                    id = 1,
                    name = "poolName",
                    slug = "poolSlug"
                )
            )
        )

        val blockDTO2 = BlockDTO(
            id = "hash2",
            timestamp = 123456789L,
            height = 2L,
            version = 1,
            bits = 1,
            nonce = 1L,
            difficulty = 1.0,
            merkleRoot = "merkleRoot",
            txCount = 100,
            size = 1000,
            weight = 4000,
            previousBlockHash = "previousBlockHash",
            extras = Extras(
                coinbaseRaw = "coinbaseRaw",
                medianFee = 1.0,
                feeRange = listOf(1.0),
                reward = 1L,
                totalFees = 1L,
                avgFee = 1.0,
                avgFeeRate = 1.0,
                pool = Pool(
                    id = 1,
                    name = "poolName",
                    slug = "poolSlug"
                )
            )
        )
        val entities = listOf(blockDTO1.toEntity(), blockDTO2.toEntity())

        coEvery { webSocketDataSource.connectToBlockUpdates() } returns flowOf(blockDTO1, blockDTO2)
        coEvery { blockDao.insertBlock(any()) } just runs
        coEvery { blockDao.deleteOldBlocks() } just runs
        coEvery { blockDao.getRecentBlocks() } returns flowOf(entities)


        val flow = repository.getBlocks()

        advanceUntilIdle()

        flow.test {
            val emittedBlocks = awaitItem()
            assertEquals(2, emittedBlocks.size)
            assertEquals(1L, emittedBlocks[0].height)
            assertEquals(2L, emittedBlocks[1].height)
            awaitComplete()
        }

        coVerify() {
            blockDao.insertBlock(any())
            blockDao.deleteOldBlocks()
        }
    }
}