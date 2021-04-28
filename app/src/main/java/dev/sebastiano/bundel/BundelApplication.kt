package dev.sebastiano.bundel

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@Suppress("unused") // It's declared in the manifest
@HiltAndroidApp
class BundelApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}
