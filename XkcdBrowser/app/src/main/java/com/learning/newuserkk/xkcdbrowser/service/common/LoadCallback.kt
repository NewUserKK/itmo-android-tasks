package com.learning.newuserkk.xkcdbrowser.service.common

interface LoadCallback<T> {
    fun onLoad(item: T)

    fun onException(error: Throwable)
}