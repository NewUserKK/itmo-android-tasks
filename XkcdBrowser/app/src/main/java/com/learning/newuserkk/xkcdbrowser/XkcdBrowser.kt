package com.learning.newuserkk.xkcdbrowser

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class XkcdBrowser: Application() {

    companion object {
        const val LOG_TAG = "Application"
        lateinit var retrofit: Retrofit
    }


    override fun onCreate() {
        super.onCreate()
        retrofit = Retrofit.Builder()
                .baseUrl(Content.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
    }

}