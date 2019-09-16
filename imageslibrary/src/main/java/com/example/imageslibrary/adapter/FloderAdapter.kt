package com.example.imageslibrary.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.imageselector.bean.FolderBean
import com.example.imageslibrary.R
import com.example.imageslibrary.utils.SelectFloderCallBack
import org.w3c.dom.Text
import java.io.File
import kotlin.math.log


/**
 * time = 2019/9/12 0012
 * CreatedName =
 */
class FloderAdapter(
    var mContext: Context,
    var mData: MutableList<FolderBean>,
    var mSelectIndex: Int
) :
    RecyclerView.Adapter<FloaderHolder>() {
    var mListener: SelectFloderCallBack? = null
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FloaderHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.adapter_folder, p0, false)
        return FloaderHolder(view)
    }

    fun setData(newData: MutableList<FolderBean>) {
        mData = newData
        notifyDataSetChanged()
    }

    fun updageIndex(newIndex: Int) {
        mSelectIndex = newIndex
        notifyDataSetChanged()
    }

    fun setOnSelectFloderCallBack(listener: SelectFloderCallBack) {
        this.mListener = listener
    }

    override fun getItemCount(): Int {
        Log.e("长度", "${mData.size}")
        return mData.size
    }

    override fun onBindViewHolder(holder: FloaderHolder, position: Int) {
//        if (!mData[position].images.isNullOrEmpty()) {
        Glide.with(mContext).load(File(mData[position].images[0].path)).into(holder.mImageView)
        holder.mTitle.text = mData[position].fileString
        holder.mSelectIV.setImageDrawable(
            if (position == mSelectIndex) mContext.resources.getDrawable(
                R.drawable.icon_image_select
            ) else mContext.resources.getDrawable(R.drawable.icon_image_un_select)
        )
        holder.mTvSize.text = "${mData[position].images.size}"
        holder.itemView.setOnClickListener {
            mListener?.onSelectFloder(position)
        }
//        }
    }

}

class FloaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mImageView: ImageView = itemView.findViewById(R.id.iv_image)
    var mTitle: TextView = itemView.findViewById(R.id.tv_folder_name)
    var mTvSize: TextView = itemView.findViewById(R.id.tv_folder_size)
    var mSelectIV: ImageView = itemView.findViewById(R.id.iv_select)

}