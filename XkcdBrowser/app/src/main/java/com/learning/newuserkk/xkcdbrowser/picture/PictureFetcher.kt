package com.learning.newuserkk.xkcdbrowser.picture

import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.learning.newuserkk.xkcdbrowser.Content
import com.learning.newuserkk.xkcdbrowser.picture.retrofit.XkcdApiService
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import java.lang.Exception
import java.net.URL


object PictureFetcher {

    const val LOG_TAG = "PictureFetcher"

    private val mapper: ObjectMapper = ObjectMapper()
    val retrofit: Retrofit

    init {
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        retrofit = Retrofit.Builder()
                .baseUrl(Content.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
    }

    @Throws(IOException::class)
    fun fetch(url: URL): XkcdComic {
        Log.i(LOG_TAG, "Fetching json from url: $url...")
        return mapper.readValue<XkcdComic>(url, XkcdComic::class.java)
    }
}