package dev.sebastiano.bundel.storage

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class RobertoModule {

    @Provides
    fun provideImagesStorage(application: Application): ImagesStorage = DiskImagesStorage(application)

    @Provides
    fun provideRobertoDatabase(application: Application): RobertoDatabase =
        Room.databaseBuilder(application, RobertoDatabase::class.java, "roberto")
            .build()
}
