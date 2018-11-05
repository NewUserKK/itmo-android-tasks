package com.learning.newuserkk.xkcdbrowser

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.learning.newuserkk.xkcdbrowser.picture.XkcdComic
import kotlinx.android.synthetic.main.images_list_content.view.*


class PictureRecyclerViewAdapter(private val parentActivity: ImagesListActivity,
                                 private val values: MutableList<XkcdComic>,
                                 private val twoPane: Boolean) :
        RecyclerView.Adapter<PictureRecyclerViewAdapter.ViewHolder>() {


    companion object {
        const val LOG_TAG = "PictureRecyclerViewAdapter"
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.id_text
        val contentView: TextView = view.content
    }


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
                        .replace(R.id.images_detail_container, fragment)
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
                .inflate(R.layout.images_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.apply {
            idView.text = idView.context.resources.getString(R.string.comicId, item.id)
            contentView.text = item.title
            itemView.tag = item
            itemView.setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size

}