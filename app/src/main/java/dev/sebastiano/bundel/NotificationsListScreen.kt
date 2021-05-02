package dev.sebastiano.bundel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sebastiano.bundel.notifications.Notification

@Preview
@Composable
fun NotificationsListEmptyLightPreview() {
    BundelTheme {
        NotificationsListScreen(notifications = listOf())
    }
}

@Preview
@Composable
fun NotificationsListLightPreview() {
    BundelTheme {
        NotificationsListScreen(
            notifications = listOf(
                Notification(timestamp = 12345678L, text = "Hello Ivan")
            )
        )
    }
}

@Composable
internal fun NotificationsListScreen(notifications: List<Notification>) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { NotificationsListTopAppBar() },
    ) {
        if (notifications.isNotEmpty()) {
            Column(Modifier.fillMaxSize()) {
                NotificationsList(notifications)
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .alpha(.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("😢", fontSize = 72.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Nothing yet...")
            }
        }
    }
}

@Composable
private fun NotificationsList(notifications: List<Notification>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(text = "Notifications", style = MaterialTheme.typography.h3, modifier = Modifier.padding(8.dp))
        }
        items(notifications) { notification ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
            ) {
                Text(notification.text ?: "[N/A]", Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
private fun NotificationsListTopAppBar() {
    TopAppBar(title = {
        Text(stringResource(id = R.string.app_name), style = MaterialTheme.typography.h4)
    })
}
