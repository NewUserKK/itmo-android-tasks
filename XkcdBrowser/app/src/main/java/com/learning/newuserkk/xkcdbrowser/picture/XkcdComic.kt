package com.learning.newuserkk.xkcdbrowser.picture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.*
import java.net.URL

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
    ) {

    companion object {
        const val LOG_TAG = "XkcdComic"
    }

    lateinit var localFile: File

    fun fetchBitmap(toPath: String): Bitmap? {
        localFile = File(toPath)
        val localPath = localFile.absolutePath

        if (localFile.exists()) {
            Log.d(LOG_TAG, "Loading saved picture from $localPath")
            return BitmapFactory.decodeFile(localPath)
        }

        if (!localFile.parentFile.exists() && !localFile.parentFile.mkdirs()) {
            throw SecurityException("Couldn't make directory $localPath")
        }

        Log.d(LOG_TAG, "Fetching image from $imgLink...")
        val inputStream = URL(imgLink).openStream().buffered()
        val outputStream = FileOutputStream(toPath).buffered()

        var i = inputStream.read()
        while (i != -1) {
            outputStream.write(i)
            i = inputStream.read()
        }

        inputStream.close()
        outputStream.close()

        Log.d(BitmapDownloadAsyncTask.LOG_TAG,"Done fetching from $imgLink")
        Log.d(BitmapDownloadAsyncTask.LOG_TAG, "Saved to $toPath")
        return BitmapFactory.decodeFile(toPath)
    }
}