package dev.sebastiano.bundel

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ApplicationModule {

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
