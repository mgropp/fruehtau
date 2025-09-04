package io.gropp.fruehtau.log

import android.util.Log
import timber.log.Timber

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority > Log.INFO) {
            Log.println(priority, tag, message)
        }
    }
}
