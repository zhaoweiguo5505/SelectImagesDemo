package com.example.imageslibrary.utils

import android.app.Activity
import android.content.Intent
import com.example.imageselector.SelectImageActivity


/**
 * time = 2019/9/16 0016
 * CreatedName = 跳转图片选择。
 */
class ImagesUtils {
    companion object {
        val SELECT_RESULT = "IMAGES_REQUEST"
        var CAMERA = "ISCAMERA" //是否开启相机选择 默认开启
        var EDITOR = "ISEDITOR" //是否编辑图片
        var MAXSELECT = "MAXSELECT" //最大选择图片 默认9张
        var COLOR = "COLOR"  //启用深色主题或者浅色主题 //默认浅色主题
        var VIDEO = "VIDEO" //是否选择视频 //默认只选择图片
        var REQUEST_CODE = 11111111
        var RESULT_CODE = 5505
        fun Builder(): ImagesUtilsBuilder {
            return ImagesUtilsBuilder()
        }

        class ImagesUtilsBuilder {
            private var isCamera = true  //是否选择相机
            private var isEditor = false  //是否编辑照片
            private var maxSelect = 9
            private var isGary = false
            private var isVideo = false

            //设置是否选择视频
            fun setVideo(isVideo: Boolean): ImagesUtilsBuilder {
                this.isVideo = isVideo
                return this
            }

            //设置是否开启相机
            fun setCamera(isCamera: Boolean): ImagesUtilsBuilder {
                this.isCamera = isCamera
                return this
            }

            //设置是否编辑图片 //只能适用于单选状态
            fun setEditor(isEditor: Boolean): ImagesUtilsBuilder {
                this.isEditor = isEditor
                return this
            }

            //设置多选最大数量
            fun setMaxSelect(maxSelect: Int): ImagesUtilsBuilder {

                this.maxSelect = maxSelect
                return this
            }

            //设置主题颜色 默认浅色
            fun setColor(isGary: Boolean): ImagesUtilsBuilder {
                this.isGary = isGary
                return this
            }

            fun start(activity: Activity) {
                var intent = Intent(activity, SelectImageActivity::class.java)
                intent.putExtra(COLOR, isGary)
                intent.putExtra(MAXSELECT, maxSelect)
                intent.putExtra(VIDEO, isVideo)
                intent.putExtra(EDITOR, isEditor)
                intent.putExtra(CAMERA, isCamera)
                activity.startActivityForResult(intent, REQUEST_CODE)
            }
        }
    }

}