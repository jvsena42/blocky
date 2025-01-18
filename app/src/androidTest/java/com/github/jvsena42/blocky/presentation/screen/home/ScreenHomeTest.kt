package com.github.jvsena42.blocky.presentation.screen.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.jvsena42.blocky.domain.model.Block
import org.junit.Before

class ScreenHomeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var homeState: HomeUiState
    private var actionPerformed: HomeActions? = null

    @Before
    fun setup() {
        homeState = HomeUiState(
            isOffline = false,
            lastUpdateTime = "2024-01-18 10:00:00",
            blocks = listOf(
                Block(
                    hash = "abc123",
                    height = 123456,
                    timestamp = 1705580400,
                    size = 1000000,
                    weight = 4000000,
                    txCount = 2500
                )
            )
        )
        actionPerformed = null
    }

    @Test
    fun topBarIsDisplayed() {
        composeTestRule.setContent {
            ScreenHome(
                uiState = homeState,
                homeActions = { action -> actionPerformed = action }
            )
        }

        composeTestRule.onNodeWithText("Bitcoin Blocks").assertExists()
        composeTestRule.onNodeWithTag("searchButton").assertExists()
    }

    @Test
    fun searchButtonClickTriggersAction() {
        composeTestRule.setContent {
            ScreenHome(
                uiState = homeState,
                homeActions = { action -> actionPerformed = action }
            )
        }

        composeTestRule.onNodeWithTag("searchButton").performClick()
        assert(actionPerformed == HomeActions.OnClickSearch)
    }

    @Test
    fun offlineCardVisibilityTest() {
        var testState by mutableStateOf(homeState.copy(isOffline = true))

        composeTestRule.setContent {
            ScreenHome(
                uiState = testState,
                homeActions = { action -> actionPerformed = action }
            )
        }

        // Check offline card is visible when offline
        composeTestRule.onNodeWithTag("offlineCard").assertExists()
        composeTestRule.onNodeWithText("You are offline").assertExists()

        // Update state to online
        testState = testState.copy(isOffline = false)

        // Check offline card is not visible when online
        composeTestRule.onNodeWithTag("offlineCard").assertDoesNotExist()
    }

    @Test
    fun lastUpdateTimeIsDisplayed() {
        composeTestRule.setContent {
            ScreenHome(
                uiState = homeState,
                homeActions = { action -> actionPerformed = action }
            )
        }

        composeTestRule.onNodeWithTag("tag_lastUpdateTime")
            .assertExists()
            .assertTextContains("Last update: ${homeState.lastUpdateTime}") //TODO MAKE CORRECT ACCESS

    }

    @Test
    fun blocksListDisplaysCorrectly() {
        composeTestRule.setContent {
            ScreenHome(
                uiState = homeState,
                homeActions = { action -> actionPerformed = action }
            )
        }

        // Verify the list exists
        composeTestRule.onNodeWithTag("blocksList").assertExists()

        // Verify block item exists and contains correct information
        val block = homeState.blocks.first()
        composeTestRule.onNodeWithTag("blockItem_${block.hash}").assertExists()
    }

    @Test
    fun emptyBlocksListTest() {
        val emptyState = homeState.copy(blocks = emptyList())

        composeTestRule.setContent {
            ScreenHome(
                uiState = emptyState,
                homeActions = { action -> actionPerformed = action }
            )
        }

        // Verify the list exists but has no items
        composeTestRule.onNodeWithTag("blocksList").assertExists()
        composeTestRule.onNodeWithTag("blocksList")
            .onChildren()
            .assertCountEquals(0)
    }

    @Test
    fun scrollingTest() {
        // Create a state with multiple blocks to test scrolling
        val multipleBlocksState = homeState.copy(
            blocks = List(20) { index ->
                Block(
                    hash = "hash$index",
                    height = index.toLong(),
                    timestamp = 1705580400 + index.toLong(),
                    size = 1000000,
                    weight = 4000000,
                    txCount = 2500
                )
            }
        )

        composeTestRule.setContent {
            ScreenHome(
                uiState = multipleBlocksState,
                homeActions = { action -> actionPerformed = action }
            )
        }

        // Verify scrolling works by checking if last item is visible after scrolling
        composeTestRule.onNodeWithTag("blocksList")
            .performScrollToIndex(19)

        composeTestRule.onNodeWithTag("blockItem_hash19")
            .assertExists()
            .assertIsDisplayed()
    }
}