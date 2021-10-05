package dev.sebastiano.bundel.storage

import android.app.Application
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sebastiano.bundel.storage.migrations.Migration1to2
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DatabaseModule {

    @Binds
    abstract fun provideImagesStorage(diskImagesStorage: DiskImagesStorage): ImagesStorage

    companion object {
        @Singleton
        @Provides
        fun provideDatabase(application: Application): RobertoDatabase =
            Room.databaseBuilder(application, RobertoDatabase::class.java, "roberto")
                .addMigrations(Migration1to2)
                .build()
    }
}
