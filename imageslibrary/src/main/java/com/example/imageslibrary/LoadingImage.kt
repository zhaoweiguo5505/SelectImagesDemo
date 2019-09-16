package com.example.imageselector

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.imageselector.bean.FolderBean
import com.example.imageselector.bean.ImageBean
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


/**
 * time = 2019/9/11 0011
 * CreatedName =
 */
class LoadingImage {
    //获取图片
    companion object {
        fun getImage(mContext: Context, callback: DataCallback) {
            GlobalScope.launch {
                var isAndroidQ = checkisAndroidQ()
                var mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                var mContentResolver = mContext.contentResolver
                var mCursor = mContentResolver.query(
                    mImageUri, arrayOf(
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.MIME_TYPE
                    ), null, null, MediaStore.Images.Media.DATE_ADDED
                )
                var images = mutableListOf<ImageBean>()
                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        val id = mCursor.getColumnIndex(MediaStore.Images.Media._ID)
                        val path = mCursor.getString(
                            mCursor.getColumnIndex(MediaStore.Images.Media.DATA)
                        )
                        val name = mCursor.getString(
                            mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                        )
                        val time =
                            mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))

                        val mimetype =
                            mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))

                        images.add(ImageBean(path, time, name, mimetype))

                    }
                    mCursor.close()
                }
                val funsplitImage = funsplitImage(images)
                callback.onSuccess(funsplitImage)
//                Log.e("测试一下", images.toString())
            }
        }

        private fun checkisAndroidQ(): Boolean {
            return Build.VERSION.SDK_INT > Build.VERSION_CODES.M
        }

        private fun getPathForAndroidQ(id: Long): String {
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                .appendPath(id.toString()).build().toString()
        }

        private fun funsplitImage(images: MutableList<ImageBean>): MutableList<FolderBean> {
            val folders = mutableListOf<FolderBean>()
            folders.add(FolderBean("全部", images))
            if (!images.isNullOrEmpty()) {
                for (item in images) {
                    val path = item.path
                    val name = getFolderName(path)
                    if (!name.isNullOrEmpty()) {
                        var imagesBean = getFolder(name, folders)
                        imagesBean.images.add(item)
                    }
                }
            }
            return folders
        }

        private fun getFolder(name: String, folders: MutableList<FolderBean>): FolderBean {
            if (folders.isNotEmpty()) {
                val size = folders.size
                for (i in 0 until size) {
                    val folder = folders[i]
                    if (name == folder.fileString) {
                        return folder
                    }
                }
            }
            val newFolder = FolderBean(name, mutableListOf())
            folders.add(newFolder)
            return newFolder
        }

        /**
         * 根据图片路径，获取图片文件夹名称
         *
         * @param path
         * @return
         */
        private fun getFolderName(path: String): String {
            if (!path.isNullOrEmpty()) {
                val strings = path.split(File.separator.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                if (strings.size >= 2) {
                    return strings[strings.size - 2]
                }
            }
            return ""
        }
    }

    interface DataCallback {
        fun onSuccess(folders: MutableList<FolderBean>)
    }
}