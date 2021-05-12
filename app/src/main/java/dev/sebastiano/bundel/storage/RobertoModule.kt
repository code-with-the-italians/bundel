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

    // TODO replace with a grown up database
    @Provides
    fun provideRobertoDatabase(application: Application): RobertoDatabase =
        Room.inMemoryDatabaseBuilder(application, RobertoDatabase::class.java)
            .build()
}
