package com.learning.newuserkk.xkcdbrowser

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

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
        setContentView(R.layout.activity_images_detail)

        if (savedInstanceState == null) {
            val fragment = ImagesDetailFragment().apply {
                arguments = Bundle().apply {
                    val id = intent.getIntExtra(ImagesDetailFragment.ARG_ITEM_ID, -1)
                    assert(id != -1)
                    putInt(ImagesDetailFragment.ARG_ITEM_ID, id)
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.images_detail_container, fragment)
                    .commit()
        }
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
