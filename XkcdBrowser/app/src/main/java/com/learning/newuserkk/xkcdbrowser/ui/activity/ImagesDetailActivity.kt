package com.learning.newuserkk.xkcdbrowser.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.learning.newuserkk.xkcdbrowser.ui.fragment.ImagesDetailFragment
import com.learning.newuserkk.xkcdbrowser.R

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
        setContentView(R.layout.activity_detail)

        if (savedInstanceState == null) {
            val fragment = ImagesDetailFragment().apply {
                arguments = Bundle().apply {
                    val id = intent.getIntExtra(ImagesDetailFragment.ARG_ITEM_ID, -1)
                    assert(id != -1)
                    putInt(ImagesDetailFragment.ARG_ITEM_ID, id)
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.detailsContainer, fragment)
                    .commit()
        }
    }
}
