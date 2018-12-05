package com.learning.newuserkk.xkcdbrowser.picture.favorites

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import com.learning.newuserkk.xkcdbrowser.PictureRecyclerViewAdapter
import com.learning.newuserkk.xkcdbrowser.XkcdBrowser
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import kotlinx.android.synthetic.main.images_list_item.*
import org.jetbrains.anko.imageResource
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference

class RestoreFavoritesAsyncTask(imageButton: ImageButton) : AsyncTask<XkcdComic, Unit, Boolean>() {

    companion object {
        const val LOG_TAG = "RestoreFavoritesAT"
    }

    private val buttonRef = WeakReference(imageButton)

    override fun doInBackground(vararg params: XkcdComic): Boolean {
        if (params.size != 1) {
            throw IllegalArgumentException("Accept one and only one argument")
        }
        val item = params[0]
        val favoritesDao = XkcdBrowser.database.favoritesDao()
        item.favorite = (item in favoritesDao.getAll())
        return item.favorite
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
        buttonRef.get()?.apply {
            imageResource = when (result) {
                true -> android.R.drawable.btn_star_big_on
                false -> android.R.drawable.btn_star_big_off
            }
        }
    }
}
