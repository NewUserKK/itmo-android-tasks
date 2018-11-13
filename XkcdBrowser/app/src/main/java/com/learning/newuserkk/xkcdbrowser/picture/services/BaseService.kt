package com.learning.newuserkk.xkcdbrowser.picture.services

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import java.util.*

const val URL_EXTRA = "com.learning.newuserkk.xkcdbrowser.picture.services.extra.url"

abstract class BaseService<T>(name: String): IntentService(name) {

    companion object {
        const val LOG_TAG = "BaseService"
    }

    val responses = ArrayDeque<T>()
    var callback: LoadCallback<T>? = null

    protected val mainHandler = Handler(Looper.getMainLooper())

    protected fun deliver(item: T?) {
        callback?.onLoad(item) ?: responses.add(item)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(LOG_TAG, "Binded service")
        return ServiceBinder(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(LOG_TAG, "Unbinded service")
        callback = null
        return super.onUnbind(intent)
    }

}