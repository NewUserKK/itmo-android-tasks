package com.learning.newuserkk.xkcdbrowser.picture

import android.os.AsyncTask
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.Content
import com.learning.newuserkk.xkcdbrowser.PictureRecyclerViewAdapter
import java.net.URL


class PictureFetchAsyncTask(private val fetcher: PictureFetcher,
                            private val adapter: PictureRecyclerViewAdapter):
        AsyncTask<URL, Unit, XkcdComic?>() {

    companion object {
        const val LOG_TAG = "PictureFetchAsyncTask"
    }

    override fun doInBackground(vararg urls: URL?): XkcdComic? {
        if (urls.size != 1) {
            throw IllegalArgumentException("Accept one and only URL")
        }
        if (urls[0] == null) {
            return null
        }
        return fetcher.fetch(urls[0]!!)
    }

    override fun onPostExecute(result: XkcdComic?) {
        if (result != null) {
            Log.d(LOG_TAG, "Fetched comic #${result.id}")
            Content.addItem(result)
            adapter.notifyDataSetChanged()
        }
    }
}