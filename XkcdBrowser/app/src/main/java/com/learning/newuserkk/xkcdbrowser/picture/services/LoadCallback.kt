package com.learning.newuserkk.xkcdbrowser.picture.services

import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic

interface LoadCallback<T> {
    fun onLoad(item: T?)
}