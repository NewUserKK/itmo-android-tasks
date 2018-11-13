package com.learning.newuserkk.xkcdbrowser.picture.services

import android.os.Binder
import android.os.Handler
import android.os.Looper

class ServiceBinder(private val service: BaseService): Binder() {
    fun setCallback(callback: LoadCallback) {
        Handler(Looper.getMainLooper()).post {
            service.callback = callback
            while (!service.responses.isEmpty()) {
                val item = service.responses.remove()
                callback.onLoad(item)
            }
        }
    }
}