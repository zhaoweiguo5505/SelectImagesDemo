package com.example.imageselector

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.example.imageselector.bean.FolderBean
import com.example.imageslibrary.R
import com.example.imageslibrary.adapter.FloderAdapter
import com.example.imageslibrary.adapter.ImagesAdapter
import com.example.imageslibrary.utils.SelectFloderCallBack
import com.example.imageslibrary.utils.SelectImageCallBack
import kotlinx.android.synthetic.main.activity_select_image.*
import java.util.*

class SelectImageActivity : AppCompatActivity() {
    var folderList = mutableListOf<FolderBean>()
    lateinit var mAdapter: ImagesAdapter
    lateinit var mfloderAdapter: FloderAdapter
    var mTitleList = mutableListOf<String>()    //相册分组
    var mImagePathList = mutableListOf<String>()  //相册每组的数据  根据 mIndexTile下标来取响应的集合
    var mIndexTitle = 0     //判断当前是哪个相册分组
    var mSelectList = mutableListOf<Boolean>()  //和相册地址对标的 是否选择
    var isOpenFlodor = false  //默认收起图片列表
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_image)
        initView()
        initData()
    }

    private fun initData() {
        LoadingImage.getImage(this, object : LoadingImage.DataCallback {
            override fun onSuccess(folders: MutableList<FolderBean>) {
                folderList = folders
                getStringPath(folderList)
                runOnUiThread {
                    mfloderAdapter.setData(folderList)
                }
            }
        })
    }

    private fun getStringPath(folderList: MutableList<FolderBean>) {
        this.runOnUiThread {
            for (item in 0 until folderList.size) {
                mTitleList.add(folderList[item].fileString)
                if (item == mIndexTitle) {
                    for (itemImages in folderList[item].images) {
                        mImagePathList.add(itemImages.path)
                        mSelectList.add(false)
                    }
                }
            }
            mImagePathList.reverse()
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun initView() {
        iv_back.setOnClickListener { finish() }
        recyclerview.layoutManager = GridLayoutManager(this, 3)
        mAdapter = ImagesAdapter(this, mImagePathList, mSelectList)
        recyclerview.adapter = mAdapter
        rv_folder.layoutManager = LinearLayoutManager(this)
        mfloderAdapter = FloderAdapter(this, folderList, mIndexTitle)
        rv_folder.adapter = mfloderAdapter
        mAdapter.setOnSelectImageCallBackListener(object : SelectImageCallBack {
            override fun onSelectImage(position: Int) {
                mSelectList[position] = !mSelectList[position]
                mAdapter.setDataIsSelect(mSelectList)
            }
        })
        mfloderAdapter.setOnSelectFloderCallBack(object : SelectFloderCallBack {
            override fun onSelectFloder(position: Int) {
                mIndexTitle = position
                mfloderAdapter.updageIndex(mIndexTitle)
                getStringPath(folderList)
            }
        })
        btn_select_images.setOnClickListener {
            if (isOpenFlodor) {
                closeFloder()
            } else {
                openFolder()
            }
        }
    }

    //打开相册选择
    private fun openFolder() {
        if (!isOpenFlodor) {
            rv_folder.visibility = View.VISIBLE
            val animator = ObjectAnimator.ofFloat(
                rv_folder, "translationY",
                rv_folder.height.toFloat(), 0f
            ).setDuration(500)
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    rv_folder.visibility = View.VISIBLE
                }
            })
            animator.start()
            isOpenFlodor = true
        }
    }

    //关闭相册选择
    private fun closeFloder() {
        if (isOpenFlodor) {
//            rv_folder.visibility = View.VISIBLE
            val animator = ObjectAnimator.ofFloat(
                rv_folder, "translationY",
                0f, rv_folder.height.toFloat()
            ).setDuration(500)
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    rv_folder.visibility = View.GONE
                }
            })
            animator.start()
            isOpenFlodor = false
        }
    }
}
