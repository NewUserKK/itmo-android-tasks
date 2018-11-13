package com.learning.newuserkk.xkcdbrowser.picture.service

import android.os.Binder
import android.os.Handler
import android.os.Looper

class ServiceBinder<T>(private val service: BaseService<T>): Binder() {
    fun setCallback(callback: LoadCallback<T>) {
        Handler(Looper.getMainLooper()).post {
            service.callback = callback
            while (!service.responses.isEmpty()) {
                val item = service.responses.remove()
                callback.onLoad(item)
            }
        }
    }
}