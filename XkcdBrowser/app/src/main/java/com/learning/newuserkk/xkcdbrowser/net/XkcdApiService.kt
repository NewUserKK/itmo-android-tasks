package com.learning.newuserkk.xkcdbrowser.net

import com.learning.newuserkk.xkcdbrowser.data.XkcdComic
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface XkcdApiService {

    @GET("{id}/info.0.json")
    fun getComic(
            @Path("id") id: Int
    ): Call<XkcdComic>

    @GET("info.0.json")
    fun getHeadComic(): Call<XkcdComic>
}