package com.learning.newuserkk.calculator

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import java.lang.Math.max
import java.lang.Math.min

class SquaredButton : Button {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val minSize = min(widthMeasureSpec, heightMeasureSpec)
//        setMeasuredDimension(minSize, minSize)

//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val height = MeasureSpec.getSize(heightMeasureSpec)
//        super.onMeasure(
//                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
//                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minDimension = min(measuredWidth, measuredHeight)
        setMeasuredDimension(minDimension, minDimension)
    }
}