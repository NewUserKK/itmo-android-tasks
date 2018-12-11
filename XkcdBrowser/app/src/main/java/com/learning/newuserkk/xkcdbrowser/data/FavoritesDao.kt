package com.learning.newuserkk.xkcdbrowser.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM favorites ORDER BY id DESC")
    suspend fun getAll(): List<XkcdComic>

    @Insert
    suspend fun insert(comic: XkcdComic, vararg comics: XkcdComic)

    @Delete
    suspend fun delete(comic: XkcdComic, vararg comics: XkcdComic)
}