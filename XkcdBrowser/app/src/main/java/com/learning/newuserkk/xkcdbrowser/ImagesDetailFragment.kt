package com.learning.newuserkk.xkcdbrowser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.learning.newuserkk.xkcdbrowser.picture.BitmapDownloadAsyncTask
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic


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

    private var item: XkcdComic? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                item = Content.ITEM_MAP[it.getInt(ARG_ITEM_ID)]
                activity?.title = item?.title
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.image_detail, container, false)

        item?.let {
            val path = context?.cacheDir?.absolutePath + "/${it.id}.png"
            BitmapDownloadAsyncTask(rootView, path).execute(it)
        }

        return rootView
    }

}
