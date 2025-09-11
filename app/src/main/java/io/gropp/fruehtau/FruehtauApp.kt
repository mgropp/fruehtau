package io.gropp.fruehtau

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.gropp.fruehtau.log.ReleaseTree
import javax.inject.Inject
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import timber.log.Timber

@HiltAndroidApp
class FruehtauApp() : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        initLogging()
        Timber.i("Fruehtau starting")
        AndroidGraphicFactory.createInstance(this)
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}
