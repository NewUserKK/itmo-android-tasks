package com.learning.newuserkk.xkcdbrowser.picture.favorites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM favorites")
    fun getAll(): List<XkcdComic>

    @Insert
    fun insert(comic: XkcdComic, vararg comics: XkcdComic)

    @Delete
    fun delete(comic: XkcdComic, vararg comics: XkcdComic)
}