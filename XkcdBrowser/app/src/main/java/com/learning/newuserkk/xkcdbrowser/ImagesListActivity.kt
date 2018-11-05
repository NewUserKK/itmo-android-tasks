package com.learning.newuserkk.xkcdbrowser

import android.Manifest.permission.INTERNET
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.learning.newuserkk.xkcdbrowser.picture.PictureFetchAsyncTask
import com.learning.newuserkk.xkcdbrowser.picture.PictureFetcher
import kotlinx.android.synthetic.main.activity_images_list.*

import kotlinx.android.synthetic.main.images_list.*
import java.lang.Math.max
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


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
    private val fetcher = PictureFetcher()
    private var loadedComicsCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_list)

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(INTERNET), 1)
            }
        }

        // present only in large-screen layouts
        if (images_detail_container != null) {
            twoPane = true
        }

        setupRecyclerView(images_list)

        // either saved when orientation changed or activity destroyed
        loadedComicsCount = Content.ITEMS.size

        Log.d(LOG_TAG, "Found $loadedComicsCount already loaded comics")
        fetchComics()

        addComicButton.text = getString(R.string.getMoreComics, COMICS_TO_ADD)
        addComicButton.setOnClickListener {
            val oldestComicId = Content.getOldestComic().id
            for (i in 1..COMICS_TO_ADD) {
                PictureFetchAsyncTask(fetcher, adapter).execute(
                        Content.getComicUrl(oldestComicId - i))
            }
        }
    }

    private fun fetchComics() {
        for (i in loadedComicsCount until Content.START_COUNT) {
            val latestComic = Content.latestComic
            if (latestComic == null) {
                if (!fetchHeadComic()) {
                    showHeadComicFetchErrorDialog()
                    break
                }

            } else {
                PictureFetchAsyncTask(fetcher, adapter).execute(
                        Content.getComicUrl(latestComic.id - i))
            }
        }
    }

    private fun fetchHeadComic(): Boolean {
        val task = PictureFetchAsyncTask(fetcher, adapter)
        try {
            Content.latestComic = task
                    .execute(Content.getComicUrl())
                    .get(5, TimeUnit.SECONDS)
            Log.d(LOG_TAG, "Fetched head comic")
            return true
        } catch (e: TimeoutException) {
            task.cancel(true)
            Log.e(LOG_TAG, "Couldn't fetch head comic")
        }

        return false
    }

    private fun showHeadComicFetchErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.headComicFetchErrorMessage))
                .setPositiveButton(getString(R.string.headComicFetchErrorReload)) { dialog, _ ->
                    dialog.dismiss()
                    fetchComics()
                }
                .setNegativeButton(getString(R.string.headComicFetchErrorExit)) { dialog, _ ->
                    dialog.dismiss()
                    this.finish()
                }
                .show()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = PictureRecyclerViewAdapter(
                this, Content.ITEMS, twoPane)
        recyclerView.adapter = adapter
    }

}
