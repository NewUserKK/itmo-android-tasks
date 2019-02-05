package com.learning.newuserkk.xkcdbrowser.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.learning.newuserkk.xkcdbrowser.ui.adapter.PictureRecyclerViewAdapter
import com.learning.newuserkk.xkcdbrowser.R
import com.learning.newuserkk.xkcdbrowser.XkcdBrowser
import com.learning.newuserkk.xkcdbrowser.XkcdBrowser.Companion.database
import com.learning.newuserkk.xkcdbrowser.data.XkcdComic
import com.learning.newuserkk.xkcdbrowser.ui.fragment.ImagesDetailFragment
import kotlinx.android.synthetic.main.list_comics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        const val LOG_TAG = "FavoritesActivity"
    }

    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job

    private lateinit var adapter: PictureRecyclerViewAdapter
    private val favoriteComics = mutableListOf<XkcdComic>()

    private var twoPane = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        if (detailsContainer != null) {
            twoPane = true
        }

        setupRecyclerView(images_list)
        launch(Dispatchers.IO) {
            favoriteComics.addAll(database.comicsDao().getFavorites())
        }
    }

    // TODO: extract listener
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = PictureRecyclerViewAdapter(favoriteComics, View.OnClickListener { view ->
            val item = view.tag as XkcdComic
            if (twoPane) {
                // передаём во фрагмент имеющийся список
                val fragment = ImagesDetailFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ImagesDetailFragment.ARG_ITEM_ID, item.id)
                    }
                }
                supportFragmentManager.beginTransaction()
                        .replace(R.id.detailsContainer, fragment)
                        .commit()
            } else {
                val intent = Intent(view.context, ImagesDetailActivity::class.java).apply {
                    putExtra(ImagesDetailFragment.ARG_ITEM_ID, item.id)
                }
                view.context.startActivity(intent)
            }
        })
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
