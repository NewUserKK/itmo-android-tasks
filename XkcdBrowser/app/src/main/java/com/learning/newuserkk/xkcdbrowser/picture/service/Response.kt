package com.learning.newuserkk.xkcdbrowser.picture.service

data class Response<T>(val item: T?, val exceptions: MutableList<Exception> = mutableListOf()) {

    /**
     * Guaranteed that [item] and [exceptions] aren't empty at the same time.
     * If item presents then there may be exceptions (usually non-critical)
     * If item is null then there must be some critical exceptions so [exceptions] not empty
     * in that case.
     */
    fun handle(callback: LoadCallback<T>?) {
        if (item == null && exceptions.isEmpty()) {
            throw IllegalStateException("Got null response with no exceptions")
        }

        if (item != null && !exceptions.isEmpty()) {
            callback?.let {
                it.onLoad(item)
                it.onException(exceptions)
            }
        } else if (!exceptions.isEmpty()) {
            callback?.onException(exceptions)
        } else if (item != null) {
            callback?.onLoad(item)
        }
    }

}