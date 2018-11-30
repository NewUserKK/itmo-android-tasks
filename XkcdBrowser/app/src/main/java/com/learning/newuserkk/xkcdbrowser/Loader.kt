package com.learning.newuserkk.xkcdbrowser

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import com.learning.newuserkk.xkcdbrowser.picture.retrofit.XkcdApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Loader {

    const val LOG_TAG = "Loader"

    /**
     * Load [count] comics from given id inclusively
     */
    fun load(from: Int,
             count: Int = 1,
             callback: (item: XkcdComic) -> Unit) {

        val service = XkcdBrowser.retrofit.create(XkcdApiService::class.java)
        for (i in 0 until count) {
            val call = if (from == -1) {
                service.getHeadComic()
            } else {
                service.getComic(from - i)
            }

            call.enqueue(object : Callback<XkcdComic> {
                override fun onFailure(call: Call<XkcdComic>, t: Throwable) {
                    // pass
                }

                override fun onResponse(call: Call<XkcdComic>, response: Response<XkcdComic>) {
                    defaultResponse(call, response, callback)
                }
            })
        }
    }

    private fun defaultResponse(call: Call<XkcdComic>,
                                response: Response<XkcdComic>,
                                callback: (item: XkcdComic) -> Unit) {
        if (response.isSuccessful) {
            val handler = Handler(Looper.getMainLooper())
            val item = response.body()!!
            handler.post {
                callback(item)
            }
        } else {
            Log.d(LOG_TAG, "Got response, response code ${response.code()}")
            Log.d(LOG_TAG, "URL: ${call.request().url()}")
        }
    }
}