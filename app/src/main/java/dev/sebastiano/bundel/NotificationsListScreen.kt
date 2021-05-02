package dev.sebastiano.bundel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun NotificationsListScreen(notifications: List<String>) {
    Column(Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text(text = "Notifications", style = MaterialTheme.typography.h3, modifier = Modifier.padding(8.dp))
            }
            items(notifications.filter { it.isNotEmpty() }) { notification ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(top = 8.dp)
                ) {
                    Text(notification, Modifier.padding(8.dp))
                }
            }
        }
    }
}
