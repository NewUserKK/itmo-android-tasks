package com.learning.newuserkk.xkcdbrowser.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.learning.newuserkk.xkcdbrowser.R
import com.learning.newuserkk.xkcdbrowser.XkcdBrowser.Companion.database
import com.learning.newuserkk.xkcdbrowser.data.XkcdComic
import kotlinx.android.synthetic.main.item_list_comics.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.imageResource
import kotlin.coroutines.CoroutineContext


open class PictureRecyclerViewAdapter(private val values: MutableList<XkcdComic>,
                                      private val onClickListener: View.OnClickListener) :
        RecyclerView.Adapter<PictureRecyclerViewAdapter.ViewHolder>(), CoroutineScope {


    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val idView: TextView = view.listItemId
        val contentView: TextView = view.listItemTitle
        val favoriteButtonView: ImageButton = view.addToFavoriteButton
    }


    companion object {
        const val LOG_TAG = "PictureRecyclerAdapter"
    }


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_comics, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.apply {
            idView.text = idView.context.resources.getString(R.string.comicId, item.id)
            contentView.text = item.title

            launch(Dispatchers.IO) {
                val favorite = database.comicsDao().getFavorites().contains(item)
                withContext(Dispatchers.Main) {
                    favoriteButtonView.imageResource = when (favorite) {
                        true -> android.R.drawable.btn_star_big_on
                        false -> android.R.drawable.btn_star_big_off
                    }
                }
            }

            favoriteButtonView.setOnClickListener {
                Log.d(LOG_TAG, "At favorite listener")
                launch(Dispatchers.IO) {
                    val favorite = database.comicsDao().getFavorites().contains(item)
                    if (!favorite) {
                        Log.d(LOG_TAG, "Adding comic #${item.id} to favorites...")
                        database.comicsDao().insert(item)
                    } else {
                        Log.d(LOG_TAG, "Deleting comic #${item.id} to favorites...")
                        database.comicsDao().delete(item)
                    }
//                    item.favorite = !item.favorite
                    withContext(Dispatchers.Main) {
                        (it as ImageButton).imageResource = when (favorite) {
                            true -> android.R.drawable.btn_star_big_on
                            false -> android.R.drawable.btn_star_big_off
                        }
                    }
                }
            }

            itemView.tag = item
            itemView.setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size
}