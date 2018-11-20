package com.learning.newuserkk.xkcdbrowser.picture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.util.Log
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.learning.newuserkk.xkcdbrowser.Content
import com.learning.newuserkk.xkcdbrowser.picture.asynctask.DownloadBitmapAsyncTask
import kotlinx.android.parcel.Parcelize
import java.io.*
import java.lang.IllegalStateException
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

    @Throws(SecurityException::class, IOException::class, IllegalStateException::class)
    fun fetchBitmap(toPath: String): Bitmap {
        val localFile = File(toPath)
        val localPath = localFile.absolutePath

        if (localFile.exists()) {
            Log.d(LOG_TAG, "Loading saved picture from $localPath")
            val bitmap = BitmapFactory.decodeFile(localPath)
            if (bitmap != null) {
                return bitmap
            }
            if (!localFile.delete()) {
                throw SecurityException("Couldn't delete file $localPath")
            }
            Log.d(LOG_TAG, "Couldn't decode bitmap for #$id, trying to reload")
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

        Log.d(LOG_TAG,"Done fetching from $imgLink")
        Log.d(LOG_TAG, "Saved to $toPath")
        return BitmapFactory.decodeFile(toPath)
                ?: throw IllegalStateException("Couldn't decode loaded picture for #$id")
    }
}