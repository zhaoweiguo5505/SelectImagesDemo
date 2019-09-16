package com.example.imageslibrary.Widget

import android.app.Activity
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupWindow
import com.example.imageselector.bean.FolderBean
import com.example.imageslibrary.R
import com.example.imageslibrary.adapter.FloderAdapter


/**
 * time = 2019/9/12 0012
 * CreatedName =
 */
class SelectImageTitlePopuwindow : PopupWindow {
    var mRecyclerView: RecyclerView
    var mAdapter: FloderAdapter

    constructor(mContext: Activity, mTitle: MutableList<FolderBean>, mIndex: Int) {
        contentView = View.inflate(mContext, R.layout.view_popupwindow, null)
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = (mContext.window.decorView.height*0.6).toInt()
        isOutsideTouchable = true
        isFocusable = true
        mRecyclerView = contentView.findViewById(R.id.popupwindow_rv)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter = FloderAdapter(mContext, mTitle, mIndex)
        mRecyclerView.adapter = mAdapter

    }
}

