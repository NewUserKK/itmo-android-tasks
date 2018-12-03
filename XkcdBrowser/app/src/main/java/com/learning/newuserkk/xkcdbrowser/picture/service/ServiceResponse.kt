package com.learning.newuserkk.xkcdbrowser.picture.service

data class ServiceResponse<T>(val item: T?, val error: Throwable? = null) {

    /**
     * Guaranteed that [item] and [error] aren't null or not null at the same time.
     * If item is null then there must be some critical error so [error] not null
     * in that case.
     */
    fun handle(callback: LoadCallback<T>?): Boolean {
        if (item == null && error == null || item != null && error != null) {
            throw IllegalStateException("Illegal response state: " +
                    "item or response either null or not null at the same time")
        }

        if (callback == null) {
            return false
        }

        assert(item != null && error == null || item == null && error != null)
        error?.let{
            callback.onException(it)
        } ?: callback.onLoad(item!!)
        return true
    }

}