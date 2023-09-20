package dev.sebastiano.bundel.storage

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import dev.sebastiano.bundel.storage.model.DbAppInfo
import dev.sebastiano.bundel.storage.model.DbNotification

@Database(
    entities = [DbNotification::class, DbAppInfo::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 2, to = 3)],
)
internal abstract class RobertoDatabase : RoomDatabase() {

    abstract fun dao(): Dao
}
