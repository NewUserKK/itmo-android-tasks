package com.learning.newuserkk.xkcdbrowser

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import com.learning.newuserkk.xkcdbrowser.picture.service.DownloadBitmapService
import com.learning.newuserkk.xkcdbrowser.picture.service.LoadCallback
import com.learning.newuserkk.xkcdbrowser.picture.service.ServiceBinder
import kotlinx.android.synthetic.main.comic_details.*
import java.io.IOException
import java.lang.IllegalStateException


/**
 * A fragment representing a single Images detail screen.
 * This fragment is either contained in a [ImagesListActivity]
 * in two-pane mode (on tablets) or a [ImagesDetailActivity]
 * on handsets.
 */
class ImagesDetailFragment : Fragment() {

    inner class BitmapLoadCallback: LoadCallback<Bitmap> {
        override fun onLoad(item: Bitmap) {
            val comic = comic
            if (comic == null) {
                Log.e(LOG_TAG, "Comic is null!")
                return
            }

            Log.d(ImagesListActivity.LOG_TAG, "Loaded picture of #${comic.id}")
            val imageView = detailsComicPicture
            imageView.setImageBitmap(item)
            imageView.contentDescription =
                    resources.getString(R.string.detailsComicDescription, comic.alt)
            detailsComicDescription.text = imageView.contentDescription
            detailsComicDate.text = resources.getString(
                    R.string.detailsComicDate,
                    comic.day,
                    comic.month,
                    comic.year)
        }

        override fun onException(errors: List<Exception>) {
            var message = ""
            for (e in errors) {
                when (e) {
                    is IOException -> message += getString(R.string.loadPictureErrorMessage)
                    is SecurityException -> message += getString(R.string.savePictureErrorMessage)
                    is IllegalStateException -> message += getString(R.string.decodePictureErrorMessage)
                }
                message += "\n"
            }
            AlertDialog.Builder(this@ImagesDetailFragment.context!!)
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.okMessage)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    companion object {
        const val ARG_ITEM_ID = "pictureDetail"
        const val LOG_TAG = "ImagesDetailFragment"
    }

    private var comic: XkcdComic? = null
    private lateinit var serviceConnection: ServiceConnection
    private var binder: ServiceBinder<Bitmap>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupServiceConnection()

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                comic = Content.ITEM_MAP[it.getInt(ARG_ITEM_ID)]
                activity?.title = comic?.title
            }
        }
        Log.d(LOG_TAG, "onCreate finished")
    }

    private fun setupServiceConnection() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                binder = service as ServiceBinder<Bitmap>
                binder?.setCallback(BitmapLoadCallback())
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                binder = null
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "onCreateView started")
        val rootView = inflater.inflate(R.layout.comic_details, container, false)

        comic?.let { comic ->
            context?.let {context ->
                val path = context.cacheDir?.absolutePath + "/${comic.id}.png"
                DownloadBitmapService.startService(rootView.context, comic, path)
                context.bindService(Intent(context, DownloadBitmapService::class.java),
                        serviceConnection,
                        Context.BIND_AUTO_CREATE)

                container?.setOnTouchListener(object : OnSwipeTouchListener(rootView.context) {
                    override fun onSwipeRight() {
                        Log.d(LOG_TAG, "Detected right swipe")
                        if (!comic.isOldest) {
                            replaceFragment(comic.id - 1)
                        } else {
                            Toast.makeText(context,
                                    getString(R.string.oldestComicReachedOnSwipe),
                                    Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onSwipeLeft() {
                        Log.d(LOG_TAG, "Detected left swipe")
                        if (!comic.isLatest) {
                            replaceFragment(comic.id + 1)
                        } else {
                            Toast.makeText(context,
                                    getString(R.string.latestComicReachedOnSwipe),
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }

        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binder != null) {
            context?.unbindService(serviceConnection)
        }
    }

    private fun replaceFragment(comicId: Int) {
        val fragment = ImagesDetailFragment().apply {
            arguments = Bundle().apply {
                putInt(ImagesDetailFragment.ARG_ITEM_ID, comicId)
            }
        }
        fragmentManager?.beginTransaction()
                ?.replace(R.id.detailsContainer, fragment)
                ?.commit()
    }
}
