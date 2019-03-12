package com.learning.newuserkk.xkcdbrowser.data

import androidx.room.*

@Dao
interface ComicsDao {

    @Query("SELECT * FROM comics ORDER BY id DESC")
    suspend fun getAll(): List<XkcdComic>

    @Query("SELECT * FROM comics WHERE favorite == 1 ORDER BY id DESC")
    suspend fun getFavorites(): List<XkcdComic>

    @Query("SELECT count(*) FROM comics")
    suspend fun getAmount(): Int

    @Query("SELECT * FROM comics WHERE (id == :id)")
    suspend fun getById(id: Int): XkcdComic

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(comic: XkcdComic, vararg comics: XkcdComic)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(comic: XkcdComic, vararg comics: XkcdComic)

    @Delete
    suspend fun delete(comic: XkcdComic, vararg comics: XkcdComic)

//    @Query("SELECT count(*) FROM comics WHERE favorite == 1")
//    suspend fun isFavorite(comic: XkcdComic): Int
}