package com.learning.newuserkk.xkcdbrowser.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.learning.newuserkk.xkcdbrowser.ui.listeners.OnSwipeTouchListener
import com.learning.newuserkk.xkcdbrowser.R
import com.learning.newuserkk.xkcdbrowser.ui.activity.ImagesListActivity
import com.learning.newuserkk.xkcdbrowser.XkcdBrowser
import com.learning.newuserkk.xkcdbrowser.XkcdBrowser.Companion.database
import com.learning.newuserkk.xkcdbrowser.data.XkcdComic
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_comic_details.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.lang.ref.WeakReference


/**
 * A fragment representing a single Images detail screen.
 * This fragment is either contained in a [ImagesListActivity]
 * in two-pane mode (on tablets) or a [ImagesDetailActivity]
 * on handsets.
 */
class ImagesDetailFragment : androidx.fragment.app.Fragment() {

    // TODO: wrap into service
    inner class BitmapLoadCallback(rootView: View): Callback {
        private val viewRef = WeakReference(rootView)

        override fun onSuccess() {
            val comic = comic
            if (comic == null) {
                Log.e(LOG_TAG, "Comic is null!")
                return
            }
            viewRef.get()?.apply {
                Log.d(ImagesListActivity.LOG_TAG, "Loaded picture of #${comic.id}")
                detailsComicPicture.contentDescription =
                        resources.getString(R.string.detailsComicDescription, comic.alt)
                detailsComicDescription.text =
                        resources.getString(R.string.detailsComicDescription, comic.alt)
                detailsComicDate.text = resources.getString(
                        R.string.detailsComicDate,
                        comic.day,
                        comic.month,
                        comic.year)
            }
        }

        override fun onError(e: java.lang.Exception?) {
            var message = ""
            message += when (e) {
                is IOException -> getString(R.string.loadPictureErrorMessage)
                is SecurityException -> getString(R.string.savePictureErrorMessage)
                else -> getString(R.string.unknownPictureErrorMessage)
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
    private var comicId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking(Dispatchers.IO) {
            launch {
                arguments?.let {
                    if (it.containsKey(ARG_ITEM_ID)) {
                        comicId = it.getInt(ARG_ITEM_ID)
                        comic = database.comicsDao().getById(comicId)
                        activity?.title = comic?.title
                        Log.d(LOG_TAG, "Creating fragment for #$comicId, title=${comic?.title}")
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_comic_details, container, false)

        with (rootView) {
            comic?.let { comic ->
                context?.let { context ->
                    Picasso.get()
                            .load(comic.imgLink)
                            .tag(LOG_TAG)
                            .into(detailsComicPicture, BitmapLoadCallback(rootView))

//                    container?.setOnTouchListener(object : OnSwipeTouchListener(rootView.context) {
//                        override fun onSwipeRight() {
//                            Log.d(LOG_TAG, "Detected right swipe")
//                            if (!comic.isOldest) {
//                                // TODO: add proper load
//                                if (comic.id - 1 !in Content.ITEM_MAP) {
////                                Loader.load(context, comic.id - 1)
//                                    Toast.makeText(context, getString(R.string.debug_oldestLoadedComicReachedOnSwipe), Toast.LENGTH_SHORT).show()
//                                } else {
//                                    replaceFragment(comic.id - 1)
//                                }
//                            } else {
//                                Toast.makeText(context, getString(R.string.oldestComicReachedOnSwipe), Toast.LENGTH_SHORT).show()
//                            }
//                        }
//
//                        override fun onSwipeLeft() {
//                            Log.d(LOG_TAG, "Detected left swipe")
//                            if (!comic.isLatest && comic.id + 1 in Content.ITEM_MAP) {
//                                replaceFragment(comic.id + 1)
//                            } else {
//                                Toast.makeText(context, getString(R.string.latestComicReachedOnSwipe), Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    })
                }
            }
        }


        return rootView
    }

    private fun replaceFragment(comicId: Int) {
        val fragment = ImagesDetailFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_ITEM_ID, comicId)
            }
        }
        fragmentManager?.beginTransaction()
                ?.replace(R.id.detailsContainer, fragment)
                ?.commit()
    }
}
