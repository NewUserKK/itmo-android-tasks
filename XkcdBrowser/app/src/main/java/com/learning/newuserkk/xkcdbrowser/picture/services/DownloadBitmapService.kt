package com.learning.newuserkk.xkcdbrowser.picture.services

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import java.io.IOException
import java.net.URL

const val COMIC_EXTRA = "com.learning.newuserkk.xkcdbrowser.picture.services.extra.comic"
const val SAVE_PATH_EXTRA = "com.learning.newuserkk.xkcdbrowser.picture.services.extra.savepath"


class DownloadBitmapService: BaseService<Bitmap>("DownloadBitmapService") {

    companion object {
        const val LOG_TAG = "DownloadBitmapService"

        fun startService(context: Context, item: XkcdComic, path: String) {
            val intent = Intent(context, DownloadBitmapService::class.java)
            intent.putExtra(COMIC_EXTRA, item)
            intent.putExtra(SAVE_PATH_EXTRA, path)
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        intent?.run {
            val item = intent.getParcelableExtra<XkcdComic>(COMIC_EXTRA)
            val path = intent.getStringExtra(SAVE_PATH_EXTRA)
            if (item != null && path != null) {
                var bitmap: Bitmap? = null
                try {
                    bitmap = item.fetchBitmap(path)
                } catch (e: SecurityException) {
                    Log.e(LOG_TAG, e.message)
                } catch (e: IOException) {
                    Log.e(LOG_TAG, e.message)
                }
                mainHandler.post {
                    deliver(bitmap)
                }
            }
        }
    }
}