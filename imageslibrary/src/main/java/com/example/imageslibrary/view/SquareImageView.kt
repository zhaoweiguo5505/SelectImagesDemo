package com.example.imageslibrary.view

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet


/**
 * time = 2019/9/12 0012
 * CreatedName =
 */
class SquareImageView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}