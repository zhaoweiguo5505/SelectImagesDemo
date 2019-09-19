package com.example.imageselector

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.imageselector.bean.FolderBean
import com.example.imageslibrary.ProviderUtil
import com.example.imageslibrary.R
import com.example.imageslibrary.adapter.FloderAdapter
import com.example.imageslibrary.adapter.ImagesAdapter
import com.example.imageslibrary.utils.ImagesUtils
import com.example.imageslibrary.utils.SelectFloderCallBack
import com.example.imageslibrary.utils.SelectImageCallBack
import kotlinx.android.synthetic.main.activity_select_image.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SelectImageActivity : AppCompatActivity() {
    var folderList = mutableListOf<FolderBean>()
    lateinit var mAdapter: ImagesAdapter
    lateinit var mfloderAdapter: FloderAdapter
    var mTitleList = mutableListOf<String>()    //相册分组
    var mImagePathList = mutableListOf<String>()  //相册每组的数据  根据 mIndexTile下标来取响应的集合
    var mIndexTitle = 0     //判断当前是哪个相册分组
    var mSelectList = mutableListOf<Boolean>()  //和相册地址对标的 是否选择
    var isOpenFlodor = false  //默认收起图片列表
    var cameraPath = ""
    private val PERMISSION_CAMERA_REQUEST_CODE = 0x00000012
    private var REQUEST_CODE_CAMERA = 11111
    var isCamera = false
    var maxSelect = 9
    var mPhotoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_image)
        intent.getBooleanExtra(ImagesUtils.CAMERA, false)
        maxSelect = intent.getIntExtra(ImagesUtils.MAXSELECT, 9)
        isCamera = intent.getBooleanExtra(
            ImagesUtils.CAMERA,
            false
        ) && maxSelect == 1
        initView()
        checkPermissionAndLoadImages()
        btn_select_images.setOnClickListener {
            if (mSelectList.contains(true)) {
                var mSelectPath = mutableListOf<String>()
                for (item in 0 until mSelectList.size) {
                    if (mSelectList[item]) {
                        mSelectPath.add(mImagePathList[item])
                    }
                }
                val toList = mSelectPath.toList() as ArrayList
                setResult(toList, false)
            } else {
                finish()
            }
        }
    }

    /**
     * 检查权限并加载SD卡里的图片。
     */
    private fun checkPermissionAndLoadImages() {
        val checkSelfPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            initData()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                0x00000011
            )
        }
    }

    private fun initData() {
        //加载手机相册图片
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

    //把相册图片转换为地址。
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
            if (isCamera) {//判断是否显示相机 只有单选状态才能选择相机
                mImagePathList.add(0, "")
                mSelectList.add(0, false)
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun initView() {
        iv_back.setOnClickListener { finish() }
        recyclerview.layoutManager = GridLayoutManager(this, 3)
        mAdapter = ImagesAdapter(this, mImagePathList, mSelectList, isCamera)
        recyclerview.adapter = mAdapter
        rv_folder.layoutManager = LinearLayoutManager(this)
        mfloderAdapter = FloderAdapter(this, folderList, mIndexTitle)
        rv_folder.adapter = mfloderAdapter
        mAdapter.setOnSelectImageCallBackListener(object : SelectImageCallBack {
            override fun onSelectImage(position: Int) {
                if (position == 0 && mImagePathList[position].isEmpty()) {
                    checkCameraPermission()
                } else if (maxSelect > 1) {// 多选模式
                    val filter = mSelectList.filter { ti -> ti }
                    if (filter.size < 9) { //判断选择是否超过9个
                        mSelectList[position] = !mSelectList[position]
                        mAdapter.setDataIsSelect(mSelectList)
                    }
                } else {
                    //单选模式
                    if (!mSelectList.contains(true)) {
                        mSelectList[position] = !mSelectList[position]
                        mAdapter.setDataIsSelect(mSelectList)
                    }
                }
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

    //打开相机拍摄
    private fun checkCameraPermission() {
        val checkSelfPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.CAMERA
        )
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.CAMERA),
                PERMISSION_CAMERA_REQUEST_CODE
            )
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var takeImageFile: File =
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
                    File(Environment.getExternalStorageDirectory(), "/DCIM/camera/")
                else
                    Environment.getDataDirectory()
            takeImageFile = createFile(takeImageFile, "IMG_", ".jpg")
            cameraPath = takeImageFile.path
            if (takeImageFile != null) {
                val uri: Uri
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    uri = Uri.fromFile(takeImageFile)
                } else {
                    uri = FileProvider.getUriForFile(
                        this,
                        ProviderUtil.getFileProviderName(this),
                        takeImageFile
                    )
                    val resInfoList = packageManager
                        .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY)
                    for (resolveInfo in resInfoList) {
                        val packageName = resolveInfo.activityInfo.packageName
                        grantUriPermission(
                            packageName,
                            uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            }

        }

        startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
    }

    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    fun createFile(folder: File, prefix: String, suffix: String): File {
        if (!folder.exists() || !folder.isDirectory) folder.mkdirs()
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
        val filename = prefix + dateFormat.format(Date(System.currentTimeMillis())) + suffix
        return File(folder, filename)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (data == null) {
                setResult(arrayListOf(cameraPath), true)
            }
        }
    }

    private fun setResult(images: ArrayList<String>, isCameraImage: Boolean) {
        val intent = Intent()
        intent.putStringArrayListExtra(ImagesUtils.SELECT_RESULT, images)
        intent.putExtra(ImagesUtils.CAMERA, isCameraImage)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * 处理权限申请的回调。
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0x00000011) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，加载图片。
                initData()
            } else {
                Toast.makeText(this, "您沒有該權限", Toast.LENGTH_LONG).show()
                //拒绝权限，弹出提示框。
//                showExceptionDialog(true)
            }
        } else if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera()
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this, "您沒有該權限", Toast.LENGTH_LONG).show()
            }
        }
    }


}
