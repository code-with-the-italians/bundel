package dev.sebastiano.bundel

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sebastiano.bundel.storage.PreferenceStorage
import dev.sebastiano.bundel.storage.SharedPreferencesStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ApplicationModule {

    @Provides
    @Singleton
    fun providePreferenceStorage(application: Application): PreferenceStorage = SharedPreferencesStorage(application)
}
