package com.example.imagesselectdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.example.imageselector.LoadingImage
import com.example.imageselector.SelectImageActivity
import com.example.imageselector.bean.FolderBean
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LoadingImage.DataCallback {
    override fun onSuccess(folders: MutableList<FolderBean>) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_select_image.setOnClickListener {
            checkPermissionAndCamera()
        }
    }

    private fun OpenActivity() {
        var intent = Intent(this, SelectImageActivity::class.java)
        startActivity(intent)
    }

    private fun checkPermissionAndCamera() {
        val checkSelfPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            OpenActivity()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                0x00000011
            )
        }
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
}
