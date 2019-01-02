package com.learning.newuserkk.xkcdbrowser.picture.service

import android.content.Context
import android.content.Intent
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.picture.PictureFetcher
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import java.io.IOException
import java.lang.Exception
import java.net.URL

const val URL_EXTRA = "com.learning.newuserkk.xkcdbrowser.picture.services.extra.url"

open class FetchComicService: BaseService<XkcdComic>("FetchComicService") {

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
                val exceptions = mutableListOf<Exception>()
                var item: XkcdComic? = null

                try {
                    item = PictureFetcher.fetch(URL(urlString))
                } catch (e: IOException) {
                    Log.e(LOG_TAG, e.message)
                    exceptions.add(e)
                }

                mainHandler.post {
                    deliver(Response(item, exceptions))
                }
            }
        }
    }
}