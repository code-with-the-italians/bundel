package dev.sebastiano.bundel

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
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
import kotlinx.serialization.json.Json
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

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager = context.packageManager

    @Provides
    @Singleton
    fun provideAssetManager(@ApplicationContext context: Context): AssetManager = context.assets

    @Provides
    @Singleton
    fun provideJson(): Json = Json.Default
}
