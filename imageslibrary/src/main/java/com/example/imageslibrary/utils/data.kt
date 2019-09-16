package com.example.imageslibrary.utils

//接口回调汇总

//图片选择回调
interface SelectImageCallBack {

    fun onSelectImage(position: Int)
}

interface SelectFloderCallBack {
    fun onSelectFloder(position: Int)
}