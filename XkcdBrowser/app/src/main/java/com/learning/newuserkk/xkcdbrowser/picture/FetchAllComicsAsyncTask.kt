package com.learning.newuserkk.xkcdbrowser.picture

import com.learning.newuserkk.xkcdbrowser.ImagesListActivity
import com.learning.newuserkk.xkcdbrowser.PictureRecyclerViewAdapter
import java.lang.ref.WeakReference

class FetchAllComicsAsyncTask(adapter: PictureRecyclerViewAdapter,
                              activity: ImagesListActivity):
        FetchComicAsyncTask(adapter) {

    private val activityRef = WeakReference(activity)

    override fun onTaskCompleted() {
        activityRef.get()?.run {
            if (this@FetchAllComicsAsyncTask.get() == null) {
                showComicsFetchErrorDialog()
            } else {
                fetchStartComics()
            }
        }
    }
}