package com.example.imageslibrary.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.imageslibrary.R
import com.example.imageslibrary.utils.SelectImageCallBack
import java.io.File


/**
 * time = 2019/9/12 0012
 * CreatedName =
 */
class ImagesAdapter(
    private var mContext: Context,
    var mImagesList: MutableList<String>,
    var mSelectList: MutableList<Boolean>,
    var isCamera: Boolean
) :
    RecyclerView.Adapter<ImagesHolder>() {
    private var selectImageCallBack: SelectImageCallBack? = null
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ImagesHolder {

        var view = LayoutInflater.from(mContext).inflate(R.layout.adapter_image, p0, false)
        return ImagesHolder(view)
    }

    override fun getItemCount(): Int = mImagesList.size
    override fun onBindViewHolder(holder: ImagesHolder, position: Int) {
        if (isCamera && position == 0) {
            holder.ivImage.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_photo_camera))
            holder.ivMasking.visibility = View.GONE
            holder.ivSelect.visibility = View.GONE
            holder.itemView.setOnClickListener {
                selectImageCallBack?.onSelectImage(position)
            }
        } else {
            Glide.with(mContext).load(File(mImagesList[position])).apply(
                RequestOptions().diskCacheStrategy(
                    DiskCacheStrategy.NONE
                )
            ).into(holder.ivImage)
            if (mSelectList.size == mImagesList.size) {
                holder.ivSelect.setImageDrawable(
                    if (mSelectList[position]) mContext.resources.getDrawable(
                        R.drawable.icon_image_select
                    ) else mContext.resources.getDrawable(R.drawable.icon_image_un_select)
                )
                holder.ivMasking.alpha = if (mSelectList[position]) 0.5f else 0.2f

            }
            holder.ivSelect.setOnClickListener {
                selectImageCallBack?.onSelectImage(position)
            }
        }
    }

    //图片选择接口回调
    fun setOnSelectImageCallBackListener(selectImageCallBack: SelectImageCallBack) {
        this.selectImageCallBack = selectImageCallBack
    }

    fun setDataIsSelect(mSelectList: MutableList<Boolean>) {
        this.mSelectList = mSelectList
        notifyDataSetChanged()
    }
}

class ImagesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var ivImage: ImageView = itemView.findViewById(R.id.iv_image)
    var ivSelect: ImageView = itemView.findViewById(R.id.iv_select)
    var ivMasking: ImageView = itemView.findViewById(R.id.iv_masking)
}

