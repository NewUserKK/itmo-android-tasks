package com.learning.newuserkk.xkcdbrowser

import android.Manifest.permission.INTERNET
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.picture.FetchComicAsyncTask
import com.learning.newuserkk.xkcdbrowser.picture.FetchAllComicsAsyncTask
import com.learning.newuserkk.xkcdbrowser.picture.PictureFetcher
import kotlinx.android.synthetic.main.activity_images_list.*

import kotlinx.android.synthetic.main.images_list.*


class ImagesListActivity : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "ImagesListActivity"
        const val COMICS_TO_ADD = 30
        const val LOADED_COMICS = "LOADED_COMICS"
        const val SHARED_PREFS_NAME = "ImagesListActivity"
    }

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane = false
    private lateinit var adapter: PictureRecyclerViewAdapter
    private var loadedComicsCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_list)

        // present only in large-screen layouts
        if (images_detail_container != null) {
            twoPane = true
        }

        setupRecyclerView(images_list)

        // either saved when orientation changed or activity destroyed
        loadedComicsCount = Content.ITEMS.size

        if (loadedComicsCount != 0) {
            adapter.notifyDataSetChanged()
        }

        Log.d(LOG_TAG, "Found $loadedComicsCount already loaded comics")
        fetchAllComics()

        addComicButton.text = getString(R.string.getMoreComics, COMICS_TO_ADD)
        addComicButton.setOnClickListener {
            val oldestComicId = Content.getOldestComic()?.id ?: return@setOnClickListener
            for (i in 1..COMICS_TO_ADD) {
                FetchComicAsyncTask(adapter).execute(
                        Content.getComicUrl(oldestComicId - i))
            }
        }
    }

    fun fetchStartComics() {
        for (i in loadedComicsCount until Content.START_COUNT) {
            val latestComic = Content.getLatestComic() ?: break
            FetchComicAsyncTask(adapter).execute(
                    Content.getComicUrl(latestComic.id - i))
        }
    }

    fun showComicsFetchErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.headComicFetchErrorMessage))
                .setPositiveButton(getString(R.string.comicFetchErrorReload)) { dialog, _ ->
                    dialog.dismiss()
                    fetchAllComics()
                }
                .setNegativeButton(getString(R.string.comicFetchErrorExit)) { dialog, _ ->
                    dialog.dismiss()
                    this.finish()
                }
                .show()
    }

    private fun fetchAllComics() {
        if (loadedComicsCount == 0) {
            val task = FetchAllComicsAsyncTask(adapter, this)
            task.execute(Content.getComicUrl())
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = PictureRecyclerViewAdapter(
                this, Content.ITEMS, twoPane)
        recyclerView.adapter = adapter
    }

}
