package com.shino72.saying

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.coroutineScope
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.shino72.saying.databinding.ActivityMainBinding
import com.shino72.saying.utils.Status
import com.shino72.saying.viewmodel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val imageViewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        imageViewModel.image.observe(this) {state ->
            when(state) {
                is Status.Loading -> {

                }
                is Status.Success -> {
                    Glide.with(this@MainActivity).load(state.data!!.photos.random().src.large).into(binding.bg)
                }
                is Status.Error -> {

                }
            }
        }


        binding.fab.setOnClickListener{
            requestPermission {
                val bitmap = getScreenShotFromView(binding.root)
                if (bitmap != null) {
                    imageViewModel.saveImage(bitmap)
                }
            }
        }

        binding.fab2.setOnClickListener{

            imageViewModel.getImage()
        }


    }

    private fun requestPermission(logic : () -> Unit) {
        TedPermission.create()
            .setPermissionListener(object :PermissionListener{
                override fun onPermissionGranted() {
                    logic()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(this@MainActivity, "권한을 허가해주세요", Toast.LENGTH_SHORT).show()
                }
            }).setDeniedMessage("사진 저장 권한을 허용해주세요.").setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE).check()
    }

    private fun getScreenShotFromView(v: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        return screenshot
    }
}