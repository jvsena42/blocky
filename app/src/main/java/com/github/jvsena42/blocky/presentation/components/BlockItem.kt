package com.github.jvsena42.blocky.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.jvsena42.blocky.R

@Composable
fun BlockItem(
    modifier: Modifier = Modifier,

    height: String,
    hash: String,
    timestamp: String,
    size: String,
    weight: String,
    txCount: String
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.height, height),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.hash, hash),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.timestamp, timestamp),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.size, size),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.weight, weight),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.transaction_count, txCount),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}