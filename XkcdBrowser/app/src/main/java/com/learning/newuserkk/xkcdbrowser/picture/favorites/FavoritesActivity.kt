package com.learning.newuserkk.xkcdbrowser.picture.favorites

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.learning.newuserkk.xkcdbrowser.Content
import com.learning.newuserkk.xkcdbrowser.PictureRecyclerViewAdapter
import com.learning.newuserkk.xkcdbrowser.R
import kotlinx.android.synthetic.main.images_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class FavoritesActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        const val LOG_TAG = "FavoritesActivity"
    }

    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job
    private lateinit var adapter: PictureRecyclerViewAdapter
    private var twoPane = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        if (detailsContainer != null) {
            twoPane = true
        }

        setupRecyclerView(images_list)
        Content.ITEM_MAP.putAll(Content.FAVORITES.map { Pair(it.id, it) })
        Log.d(LOG_TAG, "Found ${Content.FAVORITES.size} loaded favorites")
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = PictureRecyclerViewAdapter(
                this@FavoritesActivity, Content.FAVORITES, twoPane)
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
