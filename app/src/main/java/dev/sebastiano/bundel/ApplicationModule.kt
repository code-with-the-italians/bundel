package dev.sebastiano.bundel

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sebastiano.bundel.storage.PreferenceStorage
import dev.sebastiano.bundel.storage.PreferenceStorageSP

@Module
@InstallIn(SingletonComponent::class)
internal class ApplicationModule {

    @Provides
    fun provideBundelApplication(application: Application): BundelApplication = application as BundelApplication

    @Provides
    fun providePreferenceStorage(@ApplicationContext appContext: Context): PreferenceStorage {
        return PreferenceStorageSP(appContext)
    }
}
