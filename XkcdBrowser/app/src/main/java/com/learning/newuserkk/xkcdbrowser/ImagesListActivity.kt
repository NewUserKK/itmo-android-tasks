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
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import com.learning.newuserkk.xkcdbrowser.picture.service.*
import kotlinx.android.synthetic.main.images_list.*
import kotlinx.android.synthetic.main.list_activity.*


class ImagesListActivity : AppCompatActivity() {

    inner class LoadHeadComicCallback: LoadCallback<XkcdComic> {
        override fun onLoad(item: XkcdComic) {
            addComic(item)
            fetchRemainingComics()
        }

        override fun onException(error: Throwable) {
            showComicsFetchErrorDialog()
        }
    }


    inner class LoadRegularComicCallback: LoadCallback<XkcdComic> {
        override fun onLoad(item: XkcdComic) {
            addComic(item)
        }

        override fun onException(error: Throwable) {
            Toast.makeText(this@ImagesListActivity,
                    getString(R.string.loadComicJsonErrorMessage),
                    Toast.LENGTH_SHORT)
                    .show()
        }
    }



    companion object {
        const val LOG_TAG = "ImagesListActivity"
        const val START_COUNT = 30
        const val COMICS_TO_ADD = 20
    }

    private var twoPane = false

    private var service: FetchComicService? = null
    private var binder: FetchComicService.FetchComicServiceBinder? = null

    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as FetchComicService.FetchComicServiceBinder
            this@ImagesListActivity.service = binder?.service
            binder?.setHeadCallback(LoadHeadComicCallback())
            binder?.setRegularCallback(LoadRegularComicCallback())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            binder = null
        }
    }

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

        bindService(
                Intent(this, FetchComicService::class.java),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        )

        Log.d(LOG_TAG, "Found $loadedComicsCount already loaded comics")
        if (loadedComicsCount == 0) {
            fetchStartComics()

        } else {
            notifyAdapter()
        }

        addComicsButton.text = getString(R.string.getMoreComics, COMICS_TO_ADD)
        addComicsButton.setOnClickListener {
            val oldestComicId = Content.oldestLoadedComic?.id ?: return@setOnClickListener
            fetchComics(oldestComicId, COMICS_TO_ADD)
        }

        // TODO: fix bug with multiple clicks
        reloadButton.setOnClickListener {
            FetchComicService.cancelAll {
                Content.clear()
                adapter.notifyDataSetChanged()
                fetchStartComics()
            }
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = PictureRecyclerViewAdapter(
                this, Content.ITEMS, twoPane)
        recyclerView.adapter = adapter
    }

    private fun fetchStartComics() {
        val intent = Intent(this, FetchComicService::class.java).apply {
            action = LOAD_HEAD_ACTION
        }
        startService(intent)
        bindService(
                Intent(this, FetchComicService::class.java),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        )
    }

    private fun fetchRemainingComics() {
        val oldestComicId = Content.oldestLoadedComic?.id ?: return
        Log.d(LOG_TAG, "Fetching remaining comics from $oldestComicId")
        fetchComics(oldestComicId, START_COUNT)
    }

    private fun fetchComics(from: Int, count: Int = 1) {
        val intent = Intent(this, FetchComicService::class.java).apply {
            action = LOAD_REGULAR_ACTION
            putExtra(FROM_COMIC_ID_EXTRA, from)
            putExtra(COMICS_AMOUNT_TO_LOAD_EXTRA, count)
        }
        startService(intent)
    }

    private fun addComic(comic: XkcdComic) {
        Log.d(ImagesListActivity.LOG_TAG, "Got #${comic.id}")
        Content.addItem(comic)
        notifyAdapter()
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

    fun notifyAdapter() {
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binder != null) {
            unbindService(serviceConnection)
        }
    }
}
