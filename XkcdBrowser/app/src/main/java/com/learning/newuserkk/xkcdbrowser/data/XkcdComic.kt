package com.learning.newuserkk.xkcdbrowser.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

@Entity(tableName = "comics")
data class XkcdComic
    constructor(
            @PrimaryKey val id: Int,

            @ColumnInfo val title: String,
            @ColumnInfo val safeTitle: String?,
            @ColumnInfo val link: String?,
            @ColumnInfo val imgLink: String,

            @ColumnInfo val alt: String?,
            @ColumnInfo val news: String?,
            @ColumnInfo val transcript: String?,

            @ColumnInfo val year: Int,
            @ColumnInfo val month: Int,
            @ColumnInfo val day: Int,
            @ColumnInfo var favorite: Boolean
    ) {

    @JsonCreator constructor(
            @JsonProperty("num") id: Int,

            @JsonProperty("title") title: String,
            @JsonProperty("safe_title") safeTitle: String?,
            @JsonProperty("link") link: String?,
            @JsonProperty("img") imgLink: String,

            @JsonProperty("alt") alt: String?,
            @JsonProperty("news") news: String?,
            @JsonProperty("transcript") transcript: String?,

            @JsonProperty("year") year: Int,
            @JsonProperty("month") month: Int,
            @JsonProperty("day") day: Int
    ): this(id, title, safeTitle, link, imgLink, alt, news, transcript, year, month, day, false)
}