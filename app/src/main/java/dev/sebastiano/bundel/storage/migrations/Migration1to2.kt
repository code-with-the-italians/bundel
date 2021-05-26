package dev.sebastiano.bundel.storage.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal object Migration1to2: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE notifications ADD COLUMN notification_key TEXT NOT NULL default ''")
        database.execSQL("UPDATE notifications SET notification_key = cast(notification_id as TEXT)")
    }
}
