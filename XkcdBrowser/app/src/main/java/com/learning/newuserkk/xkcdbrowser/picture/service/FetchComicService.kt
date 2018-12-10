package com.learning.newuserkk.xkcdbrowser.picture.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.XkcdBrowser
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import com.learning.newuserkk.xkcdbrowser.picture.retrofit.XkcdApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


const val LOAD_HEAD_ACTION = "com.learning.newuserkk.xkcdbrowser.picture.services.action.loadHead"
const val LOAD_REGULAR_ACTION = "com.learning.newuserkk.xkcdbrowser.picture.services.action.loadRegular"

const val FROM_COMIC_ID_EXTRA = "com.learning.newuserkk.xkcdbrowser.picture.services.extra.fromComicId"
const val COMICS_AMOUNT_TO_LOAD_EXTRA = "com.learning.newuserkk.xkcdbrowser.picture.services.extra.comicsAmountToLoad"


class FetchComicService : Service() {

    class ServiceBinder(val service: FetchComicService) : Binder() {
        fun setHeadCallback(callback: LoadCallback<XkcdComic>) {
            Log.d(LOG_TAG, "Set head comic callback")
            service.headCallback = callback
            processQueue(callback)
        }

        fun setRegularCallback(callback: LoadCallback<XkcdComic>) {
            Log.d(LOG_TAG, "Set regular comic callback")
            service.regularCallback = callback
            processQueue(callback)
        }

        private fun processQueue(callback: LoadCallback<XkcdComic>) {
            while (!service.responses.isEmpty()) {
                val response = service.responses.remove()
                response.handle(callback)
            }
        }
    }


    companion object {
        const val LOG_TAG = "FetchComicService"

        var id = 0

        @SuppressLint("UseSparseArrays")
        val queries = HashMap<Int, Call<XkcdComic>>()

        fun cancelAll(callback: () -> Unit = {}) {
            for (query in queries) {
                val (id, call) = query
                Log.d(LOG_TAG, "Cancelled request $id")
                call.cancel()
            }
            queries.clear()
            Log.d(LOG_TAG, "Cancelled all queries")
            callback()
        }
    }


    val responses = ArrayDeque<ServiceResponse<XkcdComic>>()
    var headCallback: LoadCallback<XkcdComic>? = null
    var regularCallback: LoadCallback<XkcdComic>? = null

    private val retrofitService = XkcdBrowser.retrofit.create(XkcdApiService::class.java)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.apply {
            when (action) {
                LOAD_HEAD_ACTION -> loadHeadComic()

                LOAD_REGULAR_ACTION -> {
                    val from = getIntExtra(FROM_COMIC_ID_EXTRA, Int.MIN_VALUE)
                    val count = getIntExtra(COMICS_AMOUNT_TO_LOAD_EXTRA, -1)
                    if (from != Int.MIN_VALUE) {
                        loadRegularComic(from, count)
                    } else throw IllegalArgumentException("No from id in intent")
                }

                else -> throw IllegalArgumentException("Unknown action, " +
                        "must be one of FetchComicService action constants")
            }
            Log.d(LOG_TAG, "Started service")

        } ?: Log.e(LOG_TAG, "Failed to start service, intent is null")
        return super.onStartCommand(intent, flags, startId)
    }

    private fun loadHeadComic() {
        Log.d(LOG_TAG, "Loading head comic")
        makeCall(retrofitService.getHeadComic(), LOAD_HEAD_ACTION)
    }

    private fun loadRegularComic(from: Int, count: Int = -1) {
        Log.d(LOG_TAG, "Loading $count comics from $from")
        for (i in 0 until count) {
            makeCall(retrofitService.getComic(from - i), LOAD_REGULAR_ACTION)
        }
    }

    private fun makeCall(call: Call<XkcdComic>, action: String) {
        val callId = id++
        queries[callId] = call
        val handler = Handler(Looper.getMainLooper())
        call.enqueue(object : Callback<XkcdComic> {
            override fun onFailure(call: Call<XkcdComic>, t: Throwable) {
                handler.post {
                    deliver(ServiceResponse(null, t), getCallback(action))
                }
                queries.remove(callId)
            }

            override fun onResponse(call: Call<XkcdComic>, response: Response<XkcdComic>) {
                Log.d(LOG_TAG, "Got response, response code ${response.code()}")
                handler.post {
                    val serviceResponse = if (response.isSuccessful) {
                        val item = response.body()!!
                        ServiceResponse(item, null)
                    } else {
                        Log.d(LOG_TAG, "URL: ${call.request().url()}")
                        ServiceResponse<XkcdComic>(null,
                                UnknownError("Got bad response, code ${response.code()}"))
                    }
                    deliver(serviceResponse, getCallback(action))
                }
                queries.remove(callId)
            }
        })
    }

    private fun deliver(response: ServiceResponse<XkcdComic>, callback: LoadCallback<XkcdComic>?) {
        Log.d(LOG_TAG, "Delivering response")

        if (!response.handle(callback)) {
            Log.d(LOG_TAG, "Adding response to queue")
            responses.add(response)
        }
    }

    private fun getCallback(action: String): LoadCallback<XkcdComic>? {
        return when (action) {
            LOAD_HEAD_ACTION -> headCallback
            LOAD_REGULAR_ACTION -> regularCallback
            else -> throw IllegalArgumentException("Unknown action, " +
                    "must be one of FetchComicService action constants")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(LOG_TAG, "Binded service")
        return ServiceBinder(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(LOG_TAG, "Unbinded service")
        headCallback = null
        regularCallback = null
        return super.onUnbind(intent)
    }
}