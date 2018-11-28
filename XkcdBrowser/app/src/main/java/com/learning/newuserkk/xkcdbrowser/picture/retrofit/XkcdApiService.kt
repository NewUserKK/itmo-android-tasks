package com.learning.newuserkk.xkcdbrowser.picture.retrofit

import com.learning.newuserkk.xkcdbrowser.Content
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface XkcdApiService {

    @GET("{id}/${Content.BASE_URL_FILE_NAME}")
    fun getComic(
            @Path("id") id: Int
    ): Call<XkcdComic>

}