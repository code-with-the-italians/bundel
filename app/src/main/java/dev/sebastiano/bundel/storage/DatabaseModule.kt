package dev.sebastiano.bundel.storage

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sebastiano.bundel.storage.migrations.Migration1to2

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {

    @Provides
    fun provideImagesStorage(application: Application): ImagesStorage = DiskImagesStorage(application)

    @Provides
    fun provideDatabase(application: Application): RobertoDatabase =
        Room.databaseBuilder(application, RobertoDatabase::class.java, "roberto")
            .addMigrations(Migration1to2)
            .build()
}
