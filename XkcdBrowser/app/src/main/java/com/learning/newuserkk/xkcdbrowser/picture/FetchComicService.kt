package com.learning.newuserkk.xkcdbrowser.picture

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.Content
import com.learning.newuserkk.xkcdbrowser.PictureRecyclerViewAdapter
import java.net.URL
import java.util.*

class FetchComicService: IntentService("FetchComicService") {


    interface LoadCallback {
        fun onLoad(item: XkcdComic?)
    }


    class FetchComicServiceBinder(private val service: FetchComicService): Binder() {
        fun setCallback(callback: LoadCallback) {
            Handler(Looper.getMainLooper()).post {
                service.callback = callback
                while (!service.responses.isEmpty()) {
                    val item = service.responses.remove()
                    callback.onLoad(item)
                }
            }
        }
    }


    companion object {
        const val URL_EXTRA = "com.learning.newuserkk.xkcdbrowser.picture.extra.url"
        const val LOG_TAG = "FetchComicService"

        fun startService(context: Context, url: URL) {
            val intent = Intent(context, FetchComicService::class.java)
            intent.putExtra(FetchComicService.URL_EXTRA, url.toString())
            context.startService(intent)
        }
    }

    private val responses = ArrayDeque<XkcdComic>()
    private val main = Handler(Looper.getMainLooper())
    private var callback: LoadCallback? = null

    override fun onHandleIntent(intent: Intent?) {
        intent?.run {
            val urlString = getStringExtra(URL_EXTRA)
            Log.d(LOG_TAG, "Started service for $urlString")
            if (urlString != null) {
                val item = PictureFetcher.fetch(URL(urlString))
                main.post {
                    deliver(item)
                }
            }
        }
    }

    private fun deliver(item: XkcdComic?) {
        callback?.onLoad(item) ?: responses.add(item)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(LOG_TAG, "Binded service")
        return FetchComicServiceBinder(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(LOG_TAG, "Unbinded service")
        callback = null
        return super.onUnbind(intent)
    }

}