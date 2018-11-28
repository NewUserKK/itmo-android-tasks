package com.learning.newuserkk.xkcdbrowser

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.picture.PictureFetcher
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import com.learning.newuserkk.xkcdbrowser.picture.retrofit.XkcdApiService
import com.learning.newuserkk.xkcdbrowser.picture.service.FetchComicService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Loader {

    const val LOG_TAG = "Loader"

    /**
     * Load [count] comics from given id inclusively
     */
    fun load(ctx: Context, from: Int, count: Int = 1) {
        for (i in 0 until count) {
//            val comic = Content.getComicUrl(from - i)
//                    ?: break
//            FetchComicService.startService(ctx, comic)
            val service = PictureFetcher.retrofit.create(XkcdApiService::class.java)
            service.getComic(from - i).enqueue(object : Callback<XkcdComic> {
                override fun onFailure(call: Call<XkcdComic>, t: Throwable) {
                    // pass
                }

                override fun onResponse(call: Call<XkcdComic>, response: Response<XkcdComic>) {
                    Log.d(LOG_TAG, "Got response, response code ${response.code()}")
                    Log.d(LOG_TAG, "URL: ${call.request().url()}")
                    if (response.isSuccessful) {
                        val handler = Handler(Looper.getMainLooper())
                        val item = response.body()!!
                        handler.post {
                            Log.d(ImagesListActivity.LOG_TAG, "Got #${item.id}")
                            Content.addItem(item)
                            (ctx as ImagesListActivity).notifyAdapter()
                        }
                    }
                }
            })
        }
    }
}