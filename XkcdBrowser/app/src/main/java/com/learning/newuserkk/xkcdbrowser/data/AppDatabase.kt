package com.learning.newuserkk.xkcdbrowser.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [XkcdComic::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao

}