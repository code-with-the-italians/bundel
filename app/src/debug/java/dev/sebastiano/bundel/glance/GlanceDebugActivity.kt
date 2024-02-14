package dev.sebastiano.bundel.glance

import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.google.android.glance.tools.viewer.GlanceSnapshot
import com.google.android.glance.tools.viewer.GlanceViewerActivity

@OptIn(ExperimentalGlanceRemoteViewsApi::class)
class GlanceDebugActivity : GlanceViewerActivity() {

    override suspend fun getGlanceSnapshot(
        receiver: Class<out GlanceAppWidgetReceiver>,
    ): GlanceSnapshot {
        return when (receiver) {
            BundelAppWidgetReceiver::class.java -> GlanceSnapshot(
                instance = CannoliWidget(null),
                state = mutablePreferencesOf(),
            )
            else -> throw IllegalArgumentException("Unknown receiver")
        }
    }

    override fun getProviders() = listOf(BundelAppWidgetReceiver::class.java)
}
