package dev.sebastiano.bundel

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class ApplicationModule {

    @Provides
    fun provideBundelApplication(application: Application): BundelApplication =
        application as BundelApplication
}
