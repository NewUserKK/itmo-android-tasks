package com.learning.newuserkk.xkcdbrowser

import android.app.Application
import androidx.room.Room
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.learning.newuserkk.xkcdbrowser.data.AppDatabase
import com.squareup.picasso.Picasso
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class XkcdBrowser: Application() {

    companion object {
        lateinit var retrofit: Retrofit
        lateinit var database: AppDatabase
    }


    override fun onCreate() {
        super.onCreate()
        val jacksonMapper = ObjectMapper()
        jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        retrofit = Retrofit.Builder()
                .baseUrl("https://xkcd.com")
                .addConverterFactory(JacksonConverterFactory.create(jacksonMapper))
                .build()

        database = Room
                .databaseBuilder(applicationContext, AppDatabase::class.java, "AppDatabase")
                .build()

        Picasso.get().isLoggingEnabled = true
    }

}