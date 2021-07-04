package dev.sebastiano.bundel

import android.content.Context
import androidx.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sebastiano.bundel.preferences.sharedPrefsMigration
import dev.sebastiano.bundel.storage.BundelPreferencesSerializer
import dev.sebastiano.bundel.preferences.DataStorePreferenceStorage
import dev.sebastiano.bundel.preferences.PreferenceStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ApplicationModule {

    private val Context.dataStore by dataStore(
        fileName = "bundelprefs.pb",
        serializer = BundelPreferencesSerializer,
        produceMigrations = { context ->
            listOf(sharedPrefsMigration(context))
        }
    )

    @Provides
    @Singleton
    fun providePreferenceStorage(@ApplicationContext context: Context): PreferenceStorage = DataStorePreferenceStorage(context.dataStore)
}
