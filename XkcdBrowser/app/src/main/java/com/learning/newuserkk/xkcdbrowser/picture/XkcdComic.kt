package com.learning.newuserkk.xkcdbrowser.picture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.util.Log
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.learning.newuserkk.xkcdbrowser.Content
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

@Parcelize
data class XkcdComic
    @JsonCreator constructor(
            @JsonProperty("num") val id: Int,

            @JsonProperty("title") val title: String,
            @JsonProperty("safe_title") val safeTitle: String?,
            @JsonProperty("link") val link: String?,
            @JsonProperty("img") val imgLink: String,

            @JsonProperty("alt") val alt: String?,
            @JsonProperty("news") val news: String?,
            @JsonProperty("transcript") val transcript: String?,

            @JsonProperty("year") val year: Int,
            @JsonProperty("month") val month: Int,
            @JsonProperty("day") val day: Int
    ): Parcelable {

    companion object {
        const val LOG_TAG = "XkcdComic"
    }

    val isLatest
        get() = (id == Content.latestLoadedComic?.id)
    val isOldest
        get() = (id == 0)
}