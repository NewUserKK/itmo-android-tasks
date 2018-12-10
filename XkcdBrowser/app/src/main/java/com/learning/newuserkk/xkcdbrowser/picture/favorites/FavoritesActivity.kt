package com.learning.newuserkk.xkcdbrowser.picture.favorites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.*
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import kotlinx.android.synthetic.main.images_list.*
import kotlinx.coroutines.*

const val WAS_ROTATED = "com.learning.newuserkk.xkcdbrowser.picture.favorites.WAS_ROTATED"

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
        setContentView(R.layout.activity_favorites)

        if (detailsContainer != null) {
            twoPane = true
        }

        setupRecyclerView(images_list)

        if (savedInstanceState == null ||
                !savedInstanceState.getBoolean(WAS_ROTATED) ||
                Content.FAVORITES.size == 0) {
            launch {
                Log.d(LOG_TAG, "Fetching favorites from db...")
                Content.FAVORITES.clear()
                Content.FAVORITES.addAll(XkcdBrowser.database.favoritesDao().getAll())
                Content.ITEM_MAP.putAll(Content.FAVORITES.map { Pair(it.id, it) })
                adapter.notifyDataSetChanged()
                Log.d(LOG_TAG, "OK")
            }
        } else {
            Log.d(LOG_TAG, "Found ${Content.FAVORITES.size} loaded favorites")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(WAS_ROTATED, true)
        super.onSaveInstanceState(outState)
    }

    private fun setupRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        adapter = FavoritesRecyclerViewAdapter(
                this@FavoritesActivity, Content.FAVORITES, twoPane)
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
