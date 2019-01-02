package com.learning.newuserkk.xkcdbrowser.picture.asynctask

import android.os.AsyncTask
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.Content
import com.learning.newuserkk.xkcdbrowser.PictureRecyclerViewAdapter
import com.learning.newuserkk.xkcdbrowser.picture.PictureFetcher
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import java.io.IOException
import java.net.URL


open class FetchComicAsyncTask(private val adapter: PictureRecyclerViewAdapter):
        AsyncTask<URL, Unit, XkcdComic?>(), AsyncTaskCallback {

    companion object {
        const val LOG_TAG = "FetchComicAsyncTask"
    }


    override fun doInBackground(vararg urls: URL?): XkcdComic? {
        if (urls.size != 1) {
            throw IllegalArgumentException("Accept one and only URL")
        }
        val url = urls[0] ?: return null
        var comic: XkcdComic? = null
        try {
            comic = PictureFetcher.fetch(url)
        } catch (e: IOException) {
            Log.e(LOG_TAG, e.message)
        }
        return comic
    }

    override fun onPostExecute(result: XkcdComic?) {
        if (result != null) {
            Log.d(LOG_TAG, "Fetched comic #${result.id}")
            Content.addItem(result)
            adapter.notifyDataSetChanged()
        }
        onTaskCompleted()
    }

    override fun onTaskCompleted() {
        // pass
    }
}