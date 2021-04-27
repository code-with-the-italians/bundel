package dev.sebastiano.bundel

import android.app.Application
import timber.log.Timber

@Suppress("unused") // It's declared in the manifest
class BundelApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}
