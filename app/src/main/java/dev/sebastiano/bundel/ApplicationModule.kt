package dev.sebastiano.bundel

import android.content.Context
import androidx.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sebastiano.bundel.preferences.DataStorePreferences
import dev.sebastiano.bundel.preferences.Preferences
import dev.sebastiano.bundel.preferences.sharedPrefsMigration
import dev.sebastiano.bundel.storage.BundelPreferencesSerializer
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
    fun providePreferences(@ApplicationContext context: Context): Preferences = DataStorePreferences(context.dataStore)
}
