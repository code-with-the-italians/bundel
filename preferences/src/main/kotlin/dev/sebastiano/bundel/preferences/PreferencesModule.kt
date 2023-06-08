package dev.sebastiano.bundel.preferences

import android.content.Context
import androidx.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferencesModule {

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
