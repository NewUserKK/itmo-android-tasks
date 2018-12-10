package com.learning.newuserkk.xkcdbrowser

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import com.learning.newuserkk.xkcdbrowser.picture.favorites.UpdateFavoritesAsyncTask
import kotlinx.android.synthetic.main.images_list_item.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.imageResource


open class PictureRecyclerViewAdapter(private val parentActivity: AppCompatActivity,
                                      private val values: MutableList<XkcdComic>,
                                      private val twoPane: Boolean) :
        RecyclerView.Adapter<PictureRecyclerViewAdapter.ViewHolder>(), CoroutineScope {


    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val idView: TextView = view.listItemId
        val contentView: TextView = view.listItemTitle
        val favoriteButtonView: ImageButton = view.addToFavoriteButton
    }


    companion object {
        const val LOG_TAG = "PictureRecyclerAdapter"
    }


    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job
    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { view ->
            val item = view.tag as XkcdComic
            if (twoPane) {
                // передаём во фрагмент имеющийся список
                val fragment = ImagesDetailFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ImagesDetailFragment.ARG_ITEM_ID, item.id)
                    }
                }
                parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detailsContainer, fragment)
                        .commit()
            } else {
                val intent = Intent(view.context, ImagesDetailActivity::class.java).apply {
                    putExtra(ImagesDetailFragment.ARG_ITEM_ID, item.id)
                }
                view.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.images_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.apply {
            idView.text = idView.context.resources.getString(R.string.comicId, item.id)
            contentView.text = item.title

            launch {
                val favoritesDao = XkcdBrowser.database.favoritesDao()
                item.favorite = (item in favoritesDao.getAll())
                favoriteButtonView.apply {
                    imageResource = when (item.favorite) {
                        true -> android.R.drawable.btn_star_big_on
                        false -> android.R.drawable.btn_star_big_off
                    }
                }
            }
            favoriteButtonView.setOnClickListener {
                Log.d(LOG_TAG, "At favorite listener")
                UpdateFavoritesAsyncTask(it as ImageButton).execute(item)
            }

            itemView.tag = item
            itemView.setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size
}