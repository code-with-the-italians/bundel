package dev.sebastiano.bundel.glance

import androidx.compose.runtime.Composable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.text.Text

class BundelAppWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = CannoliWidget()

    private class CannoliWidget : GlanceAppWidget() {

        @Composable
        override fun Content() {
            Text(text = "I am a widget hurr durr")
        }
    }
}