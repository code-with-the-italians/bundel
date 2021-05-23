package dev.sebastiano.bundel

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sebastiano.bundel.storage.PreferenceStorage
import dev.sebastiano.bundel.storage.PreferenceStorageSP

@Module
@InstallIn(SingletonComponent::class)
internal class ApplicationModule {

    @Provides
    fun providePreferenceStorage(application: Application): PreferenceStorage = PreferenceStorageSP(application)
}
