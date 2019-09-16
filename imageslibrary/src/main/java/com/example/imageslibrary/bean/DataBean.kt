package com.example.imageselector.bean


/**
 * time = 2019/9/11 0011
 * CreatedName =
 */
data class ImageBean(val path: String, val time: Long, val name: String, val mineType: String)

data class FolderBean(val fileString: String, var images:MutableList<ImageBean>)