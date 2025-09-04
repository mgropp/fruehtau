package io.gropp.fruehtau

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.gropp.fruehtau.log.ReleaseTree
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import timber.log.Timber

@HiltAndroidApp
class FruehtauApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogging()
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
