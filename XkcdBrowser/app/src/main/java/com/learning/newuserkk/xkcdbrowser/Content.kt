package com.learning.newuserkk.xkcdbrowser

import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import java.net.URL
import java.util.*


object Content {

    const val START_COUNT = 30

    private const val BASE_URL = "https://xkcd.com"
    private const val BASE_URL_FILE_NAME = "info.0.json"

    private const val LOG_TAG = "Content"

    val ITEMS: MutableList<XkcdComic> = ArrayList()
    val ITEM_MAP: MutableMap<Int, XkcdComic> = TreeMap(
            kotlin.Comparator {
                o1, o2 -> -o1.compareTo(o2)
            }
    )

    fun getLatestComic(): XkcdComic? {
        return if (ITEMS.size > 0) ITEMS[0] else null
    }

    fun getOldestComic(): XkcdComic? {
        return if (ITEMS.size > 0) ITEMS[ITEMS.size - 1] else null
    }

    fun getComicUrl(number: Int?=null): URL? {
        if (number == null) {
            return URL("$BASE_URL/$BASE_URL_FILE_NAME")
        }
        if (number < 0) {
            return null
        }
        return URL("$BASE_URL/$number/$BASE_URL_FILE_NAME")
    }

    fun addItem(item: XkcdComic) {
        ITEM_MAP[item.id] = item
        ITEMS.clear()
        ITEMS.addAll(ITEM_MAP.values)
    }
}
