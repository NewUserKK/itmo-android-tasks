package com.learning.newuserkk.xkcdbrowser

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.detail_activity.*

/**
 * An activity representing a single Images detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [ImagesListActivity].
 */
class ImagesDetailActivity : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "ImagesDetailActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        if (savedInstanceState == null) {
            val fragment = ImagesDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ImagesDetailFragment.ARG_ITEM_ID,
                            intent.getIntExtra(ImagesDetailFragment.ARG_ITEM_ID, -1))
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.detailsContainer, fragment)
                    .commit()
        }

        detailsContainer.setOnTouchListener(object : OnSwipeTouchListener(this@ImagesDetailActivity) {
            override fun onSwipeRight() {
                Log.d(LOG_TAG, "Detected right swipe")
                Toast.makeText(this@ImagesDetailActivity, "right", Toast.LENGTH_SHORT).show()
            }

            override fun onSwipeLeft() {
                Toast.makeText(this@ImagesDetailActivity, "left", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    navigateUpTo(Intent(this, ImagesListActivity::class.java))
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}
