package com.learning.newuserkk.xkcdbrowser.picture.favorites

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.learning.newuserkk.xkcdbrowser.*
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import kotlinx.android.synthetic.main.images_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    lateinit var adapter: FavoritesRecyclerViewAdapter
    var twoPane = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)

        title = getString(R.string.favorites)
        if (detailsContainer != null) {
            twoPane = true
        }
        setupRecyclerView(images_list)

        GlobalScope.launch {
            Content.FAVORITES.clear()
            Content.FAVORITES.addAll(fetchAllFavorites())
            Content.ITEM_MAP.putAll(Content.FAVORITES.map { Pair(it.id, it)} )
        }
    }

    private fun fetchAllFavorites(): List<XkcdComic> {
        return XkcdBrowser.database.favoritesDao().getAll()
    }

    private fun setupRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        adapter = FavoritesRecyclerViewAdapter(
                this, Content.FAVORITES, twoPane)
        recyclerView.adapter = adapter
    }
}
