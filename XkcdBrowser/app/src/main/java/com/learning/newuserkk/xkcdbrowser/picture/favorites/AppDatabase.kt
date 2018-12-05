package com.learning.newuserkk.xkcdbrowser.picture.favorites

import androidx.room.Database
import androidx.room.RoomDatabase
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic

@Database(entities = [XkcdComic::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao

}