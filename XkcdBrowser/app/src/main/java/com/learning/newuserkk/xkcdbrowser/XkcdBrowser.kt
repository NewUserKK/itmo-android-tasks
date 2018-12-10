package com.learning.newuserkk.xkcdbrowser

import android.app.Application
import androidx.room.Room
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.learning.newuserkk.xkcdbrowser.picture.favorites.AppDatabase
import com.squareup.picasso.Picasso
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class XkcdBrowser: Application() {

    companion object {
        const val LOG_TAG = "Application"
        lateinit var retrofit: Retrofit
        lateinit var database: AppDatabase
        lateinit var picasso: Picasso
    }


    override fun onCreate() {
        super.onCreate()
        val jacksonMapper = ObjectMapper()
        jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        retrofit = Retrofit.Builder()
                .baseUrl(Content.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(jacksonMapper))
                .build()
        database = Room
                .databaseBuilder(applicationContext, AppDatabase::class.java, "AppDatabase")
                .build()
        picasso = Picasso.get()
        picasso.isLoggingEnabled = true
    }

}