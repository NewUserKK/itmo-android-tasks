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
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import com.learning.newuserkk.xkcdbrowser.picture.service.DownloadBitmapService
import com.learning.newuserkk.xkcdbrowser.picture.service.LoadCallback
import com.learning.newuserkk.xkcdbrowser.picture.service.ServiceBinder
import kotlinx.android.synthetic.main.image_detail.*


/**
 * A fragment representing a single Images detail screen.
 * This fragment is either contained in a [ImagesListActivity]
 * in two-pane mode (on tablets) or a [ImagesDetailActivity]
 * on handsets.
 */
class ImagesDetailFragment : Fragment() {

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
    }

    private fun setupServiceConnection() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                binder = service as ServiceBinder<Bitmap>

                binder!!.setCallback(object : LoadCallback<Bitmap> {
                    override fun onLoad(item: Bitmap?) {
                        val comic = comic
                        if (comic == null) {
                            Log.w(LOG_TAG, "Comic is null!")
                            return
                        }

                        Log.d(ImagesListActivity.LOG_TAG, "Loaded picture of #${comic.id}")
                        if (item != null) {
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
                        } else {
                            AlertDialog.Builder(this@ImagesDetailFragment.context!!)
                                    .setMessage(getString(R.string.loadPictureErrorMessage))
                                    .setPositiveButton(getString(R.string.okMessage)) { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .show()
                        }
                    }
                })
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                binder = null
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.image_detail, container, false)

        comic?.let {
            val path = context?.cacheDir?.absolutePath + "/${it.id}.png"
            DownloadBitmapService.startService(rootView.context, it, path)
            context?.bindService(Intent(context, DownloadBitmapService::class.java),
                    serviceConnection,
                    Context.BIND_AUTO_CREATE)
        }

        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binder != null) {
            context?.unbindService(serviceConnection)
        }
    }
}
