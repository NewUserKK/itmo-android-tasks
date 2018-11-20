package com.learning.newuserkk.xkcdbrowser

import android.content.Context
import com.learning.newuserkk.xkcdbrowser.picture.service.FetchComicService

object Loader {

    /**
     * Load [count] comics from given id inclusively
     */
    fun load(ctx: Context, from: Int, count: Int = 1) {
        for (i in 0 until count) {
            val comic = Content.getComicUrl(from - i)
                    ?: break
            FetchComicService.startService(ctx, comic)
        }
    }
}