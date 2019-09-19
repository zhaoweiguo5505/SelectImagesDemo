package com.example.imagesselectdemo

import android.Manifest
import android.app.Activity
import android.content.Intent

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.example.imageselector.LoadingImage

import com.example.imageselector.bean.FolderBean
import com.example.imageslibrary.utils.ImagesUtils
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.ui.ImageGridActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LoadingImage.DataCallback {
    override fun onSuccess(folders: MutableList<FolderBean>) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_select_image.setOnClickListener {
            OpenActivity()
        }
    }

    private fun OpenActivity() {
        ImagePicker.getInstance().isMultiMode = false
//        ImagePicker.getInstance().isShowCamera = true
//        val intent = Intent(this, ImageGridActivity::class.java)
//        startActivityForResult(intent, 5505)
        ImagesUtils.Builder().setMaxSelect(1)
            .setCamera(true)
            .start(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0x00000011) {
            OpenActivity()
        } else {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("測試一下$resultCode", "$requestCode")
        if (resultCode == Activity.RESULT_OK) {
            val stringArrayListExtra = data?.getStringArrayListExtra(ImagesUtils.SELECT_RESULT)
            Toast.makeText(this, "${stringArrayListExtra?.size}", Toast.LENGTH_LONG).show()
        }
    }
}
