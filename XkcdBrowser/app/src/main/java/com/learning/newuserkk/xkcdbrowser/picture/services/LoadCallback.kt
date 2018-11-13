package com.learning.newuserkk.xkcdbrowser.picture.services

import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic

interface LoadCallback {
    fun onLoad(item: XkcdComic?)
}