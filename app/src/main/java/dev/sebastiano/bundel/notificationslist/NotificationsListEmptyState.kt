package dev.sebastiano.bundel.notificationslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sebastiano.bundel.R

@Composable
internal fun NotificationsListEmptyState(modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
        Column(
            Modifier
                .fillMaxSize()
                .then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(stringResource(R.string.sad_face), fontSize = 72.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.notifications_empty_text), style = MaterialTheme.typography.bodySmall)
        }
    }
}
