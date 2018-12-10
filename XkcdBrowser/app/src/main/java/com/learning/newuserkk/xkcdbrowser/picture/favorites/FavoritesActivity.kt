package com.learning.newuserkk.xkcdbrowser.picture.favorites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.learning.newuserkk.xkcdbrowser.*
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import kotlinx.android.synthetic.main.images_list.*
import kotlinx.coroutines.*

class FavoritesActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        const val LOG_TAG = "FavoritesActivity"
    }

    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job
    private lateinit var adapter: FavoritesRecyclerViewAdapter
    private var twoPane = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)

//        title = getString(R.string.favorites)
        if (detailsContainer != null) {
            twoPane = true
        }

        setupRecyclerView(images_list)

        Log.d(LOG_TAG, "Fetching favorites from db...")
        launch {
            Content.FAVORITES.clear()
            Content.FAVORITES.addAll(fetchAllFavorites())
            Content.ITEM_MAP.putAll(Content.FAVORITES.map { Pair(it.id, it)} )
            adapter.notifyDataSetChanged()
        }
        Log.d(LOG_TAG, "OK")
    }

    private fun setupRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        adapter = FavoritesRecyclerViewAdapter(
                this@FavoritesActivity, Content.FAVORITES, twoPane)
        recyclerView.adapter = adapter
    }

    private suspend fun fetchAllFavorites(): List<XkcdComic> {
        return XkcdBrowser.database.favoritesDao().getAll()
    }
}
