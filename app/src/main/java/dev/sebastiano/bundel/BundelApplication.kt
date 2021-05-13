package dev.sebastiano.bundel

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

@Suppress("unused") // It's declared in the manifest
@HiltAndroidApp
class BundelApplication : Application(), CoroutineScope {

    override val coroutineContext = SupervisorJob()

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }

    override fun onTerminate() {
        coroutineContext.cancel(CancellationException("Application being terminated"))
        super.onTerminate()
    }
}
