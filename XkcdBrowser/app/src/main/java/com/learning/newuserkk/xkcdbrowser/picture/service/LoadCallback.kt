package com.learning.newuserkk.xkcdbrowser.picture.service

interface LoadCallback<T> {
    fun onLoad(item: T)

    fun onException(errors: List<Exception>)
}