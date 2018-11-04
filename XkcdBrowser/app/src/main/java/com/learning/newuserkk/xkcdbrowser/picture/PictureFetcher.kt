package com.learning.newuserkk.xkcdbrowser.picture

import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URL


class PictureFetcher {

    companion object {
        const val LOG_TAG = "PictureFetcher"
    }

    private val mapper: ObjectMapper = ObjectMapper()

    init {
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
    }

    fun fetch(url: URL): XkcdComic? {
        Log.i(LOG_TAG, "Fetching json from url: $url...")
        return mapper.readValue<XkcdComic>(url, XkcdComic::class.java)
    }
}