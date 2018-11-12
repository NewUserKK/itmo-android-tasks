package com.learning.newuserkk.xkcdbrowser

import android.Manifest.permission.INTERNET
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.learning.newuserkk.xkcdbrowser.picture.*
import kotlinx.android.synthetic.main.activity_images_list.*

import kotlinx.android.synthetic.main.images_list.*
import java.net.URL


class ImagesListActivity : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "ImagesListActivity"
        const val COMICS_TO_ADD = 30
    }

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane = false

    private lateinit var adapter: PictureRecyclerViewAdapter
    private lateinit var binder: FetchComicService.FetchComicServiceBinder
    private lateinit var serviceConnection: ServiceConnection
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

        serviceConnection = object: ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(LOG_TAG, "Service $name connected")
                binder = service as FetchComicService.FetchComicServiceBinder

                binder.setCallback(object: FetchComicService.LoadCallback {
                    override fun onLoad(item: XkcdComic?) {
                        Log.d(LOG_TAG, "Got $item")
                        item?.let {
                            Content.addItem(it)
                            adapter.notifyDataSetChanged()
                        } ?: Toast.makeText(this@ImagesListActivity,
                                "Could'n load some comics",
                                Toast.LENGTH_SHORT)
                                .show()
                    }
                })
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                // pass
            }
        }

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
                val comic = Content.getComicUrl(oldestComicId - i) ?: break
                FetchComicService.startService(this, comic)
            }
            bindService(Intent(this, FetchComicService::class.java),
                    serviceConnection,
                    Context.BIND_AUTO_CREATE)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
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
        if (loadedComicsCount < Content.START_COUNT) {
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
