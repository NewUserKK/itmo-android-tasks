package com.learning.newuserkk.xkcdbrowser.picture.services

import android.content.Context
import android.content.Intent
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.picture.PictureFetcher
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import java.io.IOException
import java.net.URL


class FetchComicService: BaseService<XkcdComic>("FetchComicService") {

    companion object {
        const val LOG_TAG = "FetchComicService"

        fun startService(context: Context, url: URL) {
            val intent = Intent(context, FetchComicService::class.java)
            intent.putExtra(URL_EXTRA, url.toString())
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        intent?.run {
            val urlString = getStringExtra(URL_EXTRA)
            if (urlString != null) {
                var item: XkcdComic? = null
                try {
                    item = PictureFetcher.fetch(URL(urlString))
                } catch (e: IOException) {
                    Log.e(LOG_TAG, e.message)
                }
                mainHandler.post {
                    deliver(item)
                }
            }
        }
    }
}