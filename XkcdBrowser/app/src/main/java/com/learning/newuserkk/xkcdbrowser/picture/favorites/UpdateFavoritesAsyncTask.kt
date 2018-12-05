package com.learning.newuserkk.xkcdbrowser.picture.favorites

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import com.learning.newuserkk.xkcdbrowser.XkcdBrowser
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import org.jetbrains.anko.imageResource
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference

class UpdateFavoritesAsyncTask(imageButton: ImageButton) : AsyncTask<XkcdComic, Unit, UpdateFavoritesAsyncTask.Action>() {

    enum class Action {
        INSERT,
        REMOVE,
        FAIL
    }

    companion object {
        const val LOG_TAG = "UpdateFavoritesAT"
    }

    private val buttonRef = WeakReference(imageButton)

    override fun doInBackground(vararg params: XkcdComic): Action {
        val favoritesDao = XkcdBrowser.database.favoritesDao()
        if (params.size != 1) {
            throw IllegalArgumentException("Accept one and only one argument")
        }
        val item = params[0]

        var action = Action.FAIL
        try {
            if (!item.favorite) {
                Log.d(LOG_TAG, "Adding comic #${item.id} to favorites...")
                favoritesDao.insert(item)
                item.favorite = true
                action = Action.INSERT
            } else {
                Log.d(LOG_TAG, "Deleting comic #${item.id} to favorites...")
                favoritesDao.delete(item)
                item.favorite = false
                action = Action.REMOVE
            }
            Log.d(LOG_TAG, "OK")
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message)
        }
        return action
    }

    override fun onPostExecute(result: Action) {
        super.onPostExecute(result)
        buttonRef.get()?.apply {
            when (result) {
                Action.INSERT ->
                    imageResource = android.R.drawable.btn_star_big_on
                Action.REMOVE ->
                    imageResource = android.R.drawable.btn_star_big_off
                Action.FAIL -> {
                    Toast.makeText(
                            context,
                            "Failed to make favorite, try again",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}