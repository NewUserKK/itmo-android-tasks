package com.learning.newuserkk.calculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.learning.newuserkk.calculator.R

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
