package com.learning.newuserkk.xkcdbrowser.picture

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.learning.newuserkk.xkcdbrowser.Content

@Entity(tableName = "favorites")
data class XkcdComic
    @JsonCreator constructor(
            @PrimaryKey @JsonProperty("num") val id: Int,

            @ColumnInfo @JsonProperty("title") val title: String,
            @ColumnInfo @JsonProperty("safe_title") val safeTitle: String?,
            @ColumnInfo @JsonProperty("link") val link: String?,
            @ColumnInfo @JsonProperty("img") val imgLink: String,

            @ColumnInfo @JsonProperty("alt") val alt: String?,
            @ColumnInfo @JsonProperty("news") val news: String?,
            @ColumnInfo @JsonProperty("transcript") val transcript: String?,

            @ColumnInfo @JsonProperty("year") val year: Int,
            @ColumnInfo @JsonProperty("month") val month: Int,
            @ColumnInfo @JsonProperty("day") val day: Int
    ) {

    companion object {
        const val LOG_TAG = "XkcdComic"
    }

    val isLatest
        get() = (id == Content.latestLoadedComic?.id)
    val isOldest
        get() = (id == 0)
    var favorite = false
}