package com.learning.newuserkk.xkcdbrowser

import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import com.learning.newuserkk.xkcdbrowser.picture.favorites.FavoritesActivity

class FavoritesRecyclerViewAdapter(parentActivity: FavoritesActivity,
                                   values: MutableList<XkcdComic>,
                                   twoPane: Boolean):
        PictureRecyclerViewAdapter(parentActivity, values, twoPane) {

    companion object {
        const val LOG_TAG = "FavoritesRecyclerViewAdapter"
    }



}