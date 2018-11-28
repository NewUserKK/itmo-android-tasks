package com.learning.newuserkk.xkcdbrowser

import android.Manifest.permission.INTERNET
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.learning.newuserkk.xkcdbrowser.picture.PictureFetcher
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import com.learning.newuserkk.xkcdbrowser.picture.retrofit.XkcdApiService
import com.learning.newuserkk.xkcdbrowser.picture.service.FetchComicService
import com.learning.newuserkk.xkcdbrowser.picture.service.LoadCallback
import com.learning.newuserkk.xkcdbrowser.picture.service.ServiceBinder
import kotlinx.android.synthetic.main.list_activity.*
import kotlinx.android.synthetic.main.images_list.*


class ImagesListActivity : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "ImagesListActivity"
        const val COMICS_TO_ADD = 20
    }


    private var twoPane = false

    private lateinit var adapter: PictureRecyclerViewAdapter
    private var loadedComicsCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(INTERNET), 1)
            }
        }

        // present only in large-screen layouts
        if (detailsContainer != null) {
            twoPane = true
        }

        setupRecyclerView(images_list)

        loadedComicsCount = Content.ITEMS.size

        Log.d(LOG_TAG, "Found $loadedComicsCount already loaded comics")
        if (loadedComicsCount == 0) {
            fetchStartComics()

        } else {
            notifyAdapter()
        }

        addComicsButton.text = getString(R.string.getMoreComics, COMICS_TO_ADD)
        addComicsButton.setOnClickListener {
            val oldestComicId = Content.oldestLoadedComic?.id
                    ?: return@setOnClickListener
            Loader.load(oldestComicId - 1, COMICS_TO_ADD) { item ->
                Log.d(ImagesListActivity.LOG_TAG, "Got #${item.id}")
                Content.addItem(item)
                notifyAdapter()
            }
        }

        // TODO: disabled for now, doesn't work good
        reloadButton.setOnClickListener {
            Content.clear()
            adapter.notifyDataSetChanged()
            fetchStartComics()
        }

    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = PictureRecyclerViewAdapter(
                this, Content.ITEMS, twoPane)
        recyclerView.adapter = adapter
    }

    private fun fetchRemainingComics() {
        val oldestComicId = Content.oldestLoadedComic?.id ?: return

        Loader.load(oldestComicId, Content.START_COUNT - 1) { item ->
            Log.d(ImagesListActivity.LOG_TAG, "Got #${item.id}")
            Content.addItem(item)
            notifyAdapter()
        }
    }

    fun fetchStartComics() {
        Loader.load(-1) {item ->
            Log.d(ImagesListActivity.LOG_TAG, "Got #${item.id}")
            Content.addItem(item)
            notifyAdapter()
            fetchRemainingComics()
        }
    }

    fun showComicsFetchErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.headComicFetchErrorMessage))
                .setPositiveButton(getString(R.string.reloadMessage)) { dialog, _ ->
                    dialog.dismiss()
                    fetchStartComics()
                }
                .setNegativeButton(getString(R.string.exitMessage)) { dialog, _ ->
                    dialog.dismiss()
                    this.finish()
                }
                .show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun notifyAdapter() {
        adapter.notifyDataSetChanged()
    }
}
