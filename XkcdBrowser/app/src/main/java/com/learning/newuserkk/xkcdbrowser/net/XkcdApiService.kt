package com.learning.newuserkk.xkcdbrowser.net

import com.learning.newuserkk.xkcdbrowser.data.Content
import com.learning.newuserkk.xkcdbrowser.data.XkcdComic
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface XkcdApiService {

    @GET("{id}/${Content.BASE_URL_FILE_NAME}")
    fun getComic(
            @Path("id") id: Int
    ): Call<XkcdComic>

    @GET(Content.BASE_URL_FILE_NAME)
    fun getHeadComic(): Call<XkcdComic>
}