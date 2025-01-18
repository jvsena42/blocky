package com.github.jvsena42.blocky.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.github.jvsena42.blocky.R
import com.github.jvsena42.blocky.domain.model.Block
import com.github.jvsena42.blocky.presentation.components.BlockItem
import com.github.jvsena42.blocky.presentation.ui.theme.BlockyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHome(
    uiState: HomeUiState,
    onSearchClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.bitcoin_blocks)) },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isOffline) {
                Text(
                    text = "You are offline",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Text(
                text = "Last update: ${uiState.lastUpdateTime}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(uiState.blocks, key = { block -> block.hash }) { block ->
                    BlockItem(  //TODO CREATE A MODEL FOR THE VIEW
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        height = block.height.toString(),
                        hash = block.hash.toString(),
                        timestamp = block.timestamp.toString(),
                        size = block.size.toString(),
                        weight = block.weight.toString(),
                        txCount = block.txCount.toString()
                    )
                }
            }
        }
    }

}

@PreviewLightDark
@Composable
private fun Preview() {
    BlockyTheme {
        ScreenHome(HomeUiState(
            blocks = listOf(
                Block(
                    height = 1,
                    hash = "0000000000000000000",
                    timestamp = 1629264000,
                    size = 1000,
                    weight = 1000,
                    txCount = 1000
                ),
                Block(
                    height = 2,
                    hash = "0000000000000000001",
                    timestamp = 1629264000,
                    size = 1000,
                    weight = 1000,
                    txCount = 1000
                ),
            ),
            lastUpdateTime = "18/08/2021 12:00:00",
            isOffline = false
        )) { }
    }
}