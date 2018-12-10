package com.learning.newuserkk.xkcdbrowser

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import com.learning.newuserkk.xkcdbrowser.picture.service.COMICS_AMOUNT_TO_LOAD_EXTRA
import com.learning.newuserkk.xkcdbrowser.picture.service.FROM_COMIC_ID_EXTRA
import com.learning.newuserkk.xkcdbrowser.picture.service.FetchComicService
import com.learning.newuserkk.xkcdbrowser.picture.service.LOAD_REGULAR_ACTION
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.comic_details.view.*
import java.io.IOException


/**
 * A fragment representing a single Images detail screen.
 * This fragment is either contained in a [ImagesListActivity]
 * in two-pane mode (on tablets) or a [ImagesDetailActivity]
 * on handsets.
 */
class ImagesDetailFragment : androidx.fragment.app.Fragment() {

    // TODO: wrap into service
    inner class BitmapLoadCallback(private val rootView: View): Callback {
        override fun onSuccess() {
            val comic = comic
            if (comic == null) {
                Log.e(LOG_TAG, "Comic is null!")
                return
            }

            Log.d(ImagesListActivity.LOG_TAG, "Loaded picture of #${comic.id}")
            rootView.detailsComicPicture.contentDescription =
                    resources.getString(R.string.detailsComicDescription, comic.alt)
            rootView.detailsComicDescription.text =
                    resources.getString(R.string.detailsComicDescription, comic.alt)
            rootView.detailsComicDate.text = resources.getString(
                    R.string.detailsComicDate,
                    comic.day,
                    comic.month,
                    comic.year)
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
    private var wasLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                comicId = it.getInt(ARG_ITEM_ID)
                comic = Content.ITEM_MAP[comicId]
                activity?.title = comic?.title
                Log.d(LOG_TAG, "Creating fragment for #$comicId, title=${comic?.title}")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.comic_details, container, false)

        with (rootView) {
            if (comic == null) {
                comic = Content.ITEM_MAP[comicId]
            }
            comic?.let { comic ->
                context?.let { context ->
                    XkcdBrowser.picasso
                            .load(comic.imgLink)
                            .tag(LOG_TAG)
                            .into(detailsComicPicture, BitmapLoadCallback(rootView))

                    container?.setOnTouchListener(object : OnSwipeTouchListener(rootView.context) {
                        override fun onSwipeRight() {
                            Log.d(LOG_TAG, "Detected right swipe")
                            if (!comic.isOldest) {
                                // TODO: add proper load
                                if (comic.id - 1 !in Content.ITEM_MAP) {
//                                Loader.load(context, comic.id - 1)
                                    Toast.makeText(context, getString(R.string.debug_oldestLoadedComicReachedOnSwipe), Toast.LENGTH_SHORT).show()
                                } else {
                                    replaceFragment(comic.id - 1)
                                }
                            } else {
                                Toast.makeText(context, getString(R.string.oldestComicReachedOnSwipe), Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onSwipeLeft() {
                            Log.d(LOG_TAG, "Detected left swipe")
                            if (!comic.isLatest && comic.id + 1 in Content.ITEM_MAP) {
                                replaceFragment(comic.id + 1)
                            } else {
                                Toast.makeText(context, getString(R.string.latestComicReachedOnSwipe), Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
        }


        return rootView
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
