package com.learning.newuserkk.xkcdbrowser.ui.activity

import android.Manifest.permission.INTERNET
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.learning.newuserkk.xkcdbrowser.ui.adapter.PictureRecyclerViewAdapter
import com.learning.newuserkk.xkcdbrowser.R
import com.learning.newuserkk.xkcdbrowser.XkcdBrowser
import com.learning.newuserkk.xkcdbrowser.XkcdBrowser.Companion.database
import com.learning.newuserkk.xkcdbrowser.data.XkcdComic
import com.learning.newuserkk.xkcdbrowser.service.*
import com.learning.newuserkk.xkcdbrowser.service.common.LoadCallback
import com.learning.newuserkk.xkcdbrowser.ui.fragment.ImagesDetailFragment
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.list_comics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

//const val LOADED_COMICS_COUNT_EXTRA = "com.learning.newuserkk.xkcdbrowser.ui.activity.extra.loadedComicsCount"


class ImagesListActivity : AppCompatActivity(), CoroutineScope {

    inner class LoadHeadComicCallback: LoadCallback<XkcdComic> {
        override fun onLoad(item: XkcdComic) {
            addComic(item)
            fetchRemainingComics()
        }

        override fun onException(error: Throwable) {
            Log.e(LOG_TAG, error.message)
            showComicsFetchErrorDialog()
        }
    }


    inner class LoadRegularComicCallback: LoadCallback<XkcdComic> {
        override fun onLoad(item: XkcdComic) {
            addComic(item)
        }

        override fun onException(error: Throwable) {
            Log.e(LOG_TAG, error.message)
//            Toast.makeText(this@ImagesListActivity,
//                    getString(R.string.loadComicJsonErrorMessage),
//                    Toast.LENGTH_SHORT)
//                    .show()
            reloadButton.isEnabled = true
        }
    }

    inner class LoadEndComicCallback: LoadCallback<XkcdComic> {
        override fun onLoad(item: XkcdComic) {
            addComic(item)
            comics.sortByDescending { it.id }
            notifyAdapter()
            reloadButton.isEnabled = true
        }

        override fun onException(error: Throwable) {
            Log.e(LOG_TAG, error.message)
//            Toast.makeText(this@ImagesListActivity,
//                    getString(R.string.loadComicJsonErrorMessage),
//                    Toast.LENGTH_SHORT)
//                    .show()
        }
    }


    companion object {
        const val LOG_TAG = "ImagesListActivity"
        const val START_COUNT = 30
        const val COMICS_TO_ADD = 20
    }

    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job

    private var twoPane = false

    private var service: FetchComicService? = null
    private var binder: FetchComicService.ServiceBinder? = null

    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as FetchComicService.ServiceBinder
            this@ImagesListActivity.service = binder?.service
            binder?.setHeadCallback(LoadHeadComicCallback())
            binder?.setRegularCallback(LoadRegularComicCallback())
            binder?.setEndCallback(LoadEndComicCallback())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            binder = null
        }
    }

    private lateinit var adapter: PictureRecyclerViewAdapter
    private val comics = mutableListOf<XkcdComic>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // present only in large-screen layouts
        if (detailsContainer != null) {
            twoPane = true
        }

        setupRecyclerView(images_list)

        launch {
            if (savedInstanceState == null) {
                Log.d(LOG_TAG, "Fetching comics from the Internet")
                fetchStartComics()

            } else {
                comics.addAll(database.comicsDao().getAll())
                Log.d(LOG_TAG, "Loaded ${comics.size} comics from database")
            }
        }

        bindService(
                Intent(this, FetchComicService::class.java),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        )

        addComicsButton.text = getString(R.string.getMoreComics, COMICS_TO_ADD)
        addComicsButton.setOnClickListener {
            val oldestComicId = comics.lastOrNull()?.id ?: return@setOnClickListener
            fetchComics(oldestComicId, COMICS_TO_ADD)
        }

        // TODO: fix bug with multiple clicks
        reloadButton.setOnClickListener {
            FetchComicService.cancelAll {
                comics.clear()
                adapter.notifyDataSetChanged()
                fetchStartComics()
            }
        }
    }

    private fun setupRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        adapter = PictureRecyclerViewAdapter(comics, View.OnClickListener { view ->
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

    private fun fetchStartComics() {
        val intent = Intent(this, FetchComicService::class.java).apply {
            action = LOAD_HEAD_ACTION
        }
        startService(intent)
        reloadButton.isEnabled = false
    }

    private fun fetchRemainingComics() {
        val oldestComicId = comics.lastOrNull()?.id ?: return
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
        Log.d(LOG_TAG, "Got #${comic.id}")
        comics.add(comic)
    }

    fun showComicsFetchErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.headComicFetchErrorMessage))
                .setPositiveButton(getString(R.string.reload)) { dialog, _ ->
                    dialog.dismiss()
                    fetchStartComics()
                }
                .setNegativeButton(getString(R.string.exit)) { dialog, _ ->
                    dialog.dismiss()
                    this.finish()
                }
                .show()
    }

    fun notifyAdapter() {
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_list_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menuFavorites -> {
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuExit -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        launch(Dispatchers.IO) {
            comics.forEach {
                database.comicsDao().insert(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binder != null) {
            unbindService(serviceConnection)
        }
    }
}
