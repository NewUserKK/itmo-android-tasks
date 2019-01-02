package com.learning.newuserkk.xkcdbrowser.picture.asynctask

import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.learning.newuserkk.xkcdbrowser.R
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import kotlinx.android.synthetic.main.image_detail.view.*
import java.io.IOException
import java.lang.ref.WeakReference


class DownloadBitmapAsyncTask(view: View, private val savePath: String):
        AsyncTask<XkcdComic, Unit, Pair<XkcdComic, Bitmap?>>() {

    companion object {
        const val LOG_TAG = "DownloadBitmapAsyncTask"
    }

    private val viewRef = WeakReference(view)

    override fun doInBackground(vararg params: XkcdComic): Pair<XkcdComic, Bitmap?> {
        if (params.size != 1) {
            throw IllegalArgumentException("Accept one and only comic")
        }
        val comic = params[0]
        var bitmap: Bitmap? = null
        try {
            bitmap = comic.fetchBitmap(savePath)
        } catch (e: SecurityException) {
            Log.e(LOG_TAG, e.message)
        } catch (e: IOException) {
            Log.e(LOG_TAG, e.message)
        }
        return Pair(comic, bitmap)
    }

    override fun onPostExecute(result: Pair<XkcdComic, Bitmap?>) {
        val view = viewRef.get()
        val comic = result.first
        val bitmap = result.second
        view?.apply {
            if (bitmap == null) {
                detailsComicDescription.text = resources.getString(R.string.loadPictureErrorMessage)
                return
            }

            val imageView = detailsComicPicture
            imageView.setImageBitmap(bitmap)
            imageView.contentDescription =
                    resources.getString(R.string.detailsComicDescription, comic.alt)
            detailsComicDescription.text = imageView.contentDescription
            rootView.detailsComicDate.text = resources.getString(
                    R.string.detailsComicDate,
                    comic.day,
                    comic.month,
                    comic.year)
        }
    }
}
