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
import com.learning.newuserkk.xkcdbrowser.picture.service.FetchComicService
import com.learning.newuserkk.xkcdbrowser.picture.service.LoadCallback
import com.learning.newuserkk.xkcdbrowser.picture.service.ServiceBinder
import kotlinx.android.synthetic.main.list_activity.*
import kotlinx.android.synthetic.main.images_list.*


class ImagesListActivity : AppCompatActivity() {

    inner class LoadComicCallback: LoadCallback<XkcdComic> {
        override fun onLoad(item: XkcdComic) {
            Log.d(LOG_TAG, "Got #${item.id}")
                Content.addItem(item)
                adapter.notifyDataSetChanged()
            }

        override fun onException(errors: List<Exception>) {
            Toast.makeText(this@ImagesListActivity,
                    getString(R.string.loadComicJsonErrorMessage),
                    Toast.LENGTH_SHORT)
                    .show()
        }
    }

    inner class LoadHeadComicCallback: LoadCallback<XkcdComic> {
        override fun onLoad(item: XkcdComic) {
            Log.d(LOG_TAG, "Got #${item.id}")
            Content.addItem(item)
            adapter.notifyDataSetChanged()
            fetchRemainingComics()
        }

        override fun onException(errors: List<Exception>) {
            showComicsFetchErrorDialog()
        }
    }


    companion object {
        const val LOG_TAG = "ImagesListActivity"
        const val COMICS_TO_ADD = 20
    }


    private var twoPane = false

    private lateinit var adapter: PictureRecyclerViewAdapter
    private var binder: ServiceBinder<XkcdComic>? = null
    private lateinit var serviceConnection: ServiceConnection
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
            serviceConnection = getDefaultServiceConnection()
            bindService(Intent(this, FetchComicService::class.java),
                    serviceConnection,
                    Context.BIND_AUTO_CREATE)
            adapter.notifyDataSetChanged()
        }

        addComicsButton.text = getString(R.string.getMoreComics, COMICS_TO_ADD)
        addComicsButton.setOnClickListener {
            val oldestComicId = Content.getOldestComic()?.id ?: return@setOnClickListener
            for (i in 1..COMICS_TO_ADD) {
                val comic = Content.getComicUrl(oldestComicId - i) ?: break
                FetchComicService.startService(this, comic)
            }

            bindService(Intent(this, FetchComicService::class.java),
                    serviceConnection,
                    Context.BIND_AUTO_CREATE)
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

    private fun getDefaultServiceConnection(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                binder = service as ServiceBinder<XkcdComic>
                binder!!.setCallback(LoadComicCallback())
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                binder = null
            }
        }
    }

    private fun fetchRemainingComics() {
        unbindService(serviceConnection)
        serviceConnection = getDefaultServiceConnection()

        val oldestComicId = Content.getOldestComic()?.id ?: return
        for (i in loadedComicsCount until Content.START_COUNT) {
            val comic = Content.getComicUrl(oldestComicId - i) ?: break
            FetchComicService.startService(this, comic)
        }

        bindService(Intent(this, FetchComicService::class.java),
                serviceConnection,
                Context.BIND_AUTO_CREATE)
    }


    fun fetchStartComics() {
        FetchComicService.startService(this, Content.getComicUrl()!!)

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                binder = service as ServiceBinder<XkcdComic>
                binder!!.setCallback(LoadHeadComicCallback())
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                binder = null
            }
        }

        bindService(Intent(this, FetchComicService::class.java),
                serviceConnection,
                Context.BIND_AUTO_CREATE)
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

    override fun unbindService(connection: ServiceConnection) {
        if (binder != null) {
            super.unbindService(serviceConnection)
        }
        binder = null
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}
