package dev.sebastiano.bundel.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.sebastiano.bundel.storage.model.DbNotification

@Database(entities = [DbNotification::class], version = 1)
internal abstract class RobertoDatabase : RoomDatabase() {

    abstract fun robertooo(): RobertoDao
}
