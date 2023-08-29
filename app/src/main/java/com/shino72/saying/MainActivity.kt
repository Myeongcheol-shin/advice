package com.shino72.saying

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.coroutineScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.Resource
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.shino72.saying.databinding.ActivityMainBinding
import com.shino72.saying.utils.Status
import com.shino72.saying.viewmodel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val imageViewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        lifecycle.coroutineScope.launchWhenCreated {
            imageViewModel.save.collect {
                if (it.isLoading) {
                    binding.status.text = "Loading..."
                    binding.progress.visibility = View.VISIBLE
                    binding.status.visibility = View.VISIBLE
                    binding.status.text = "Save Advice..."
                }
                else if (it.error.isNotBlank()) {
                    binding.status.text = "Error"
                    binding.progress.visibility = View.INVISIBLE
                }
                it.data?.let {
                    binding.status.text = "Success"
                    binding.progress.visibility = View.INVISIBLE
                    Toast.makeText(applicationContext, "Save Gallery",Toast.LENGTH_SHORT).show()
                }
            }
        }

        imageViewModel.image.observe(this) {state ->
            when(state) {
                is Status.Loading -> {
                    binding.status.text = "Loading..."
                    binding.progress.visibility = View.VISIBLE
                    binding.status.visibility = View.VISIBLE
                }
                is Status.Success -> {
                    binding.status.text = "Success"
                    binding.progress.visibility = View.INVISIBLE
                    val rd = state.data!!.photos.random()
                    Glide.with(this@MainActivity).load(rd.src.large).transform(BlurTransformation(35, 1)
                    ).into(binding.bg)

                    binding.photographer.text = rd.photographer
                    binding.link.text = rd.photographer_url
                }
                is Status.Error -> {
                    binding.status.text = "Error"
                    binding.progress.visibility = View.INVISIBLE
                }
            }
        }

        imageViewModel.advice.observe(this) {
            state ->
            when(state) {
                is Status.Loading -> {
                    binding.status.text = "Loading..."
                    binding.progress.visibility = View.VISIBLE
                    binding.status.visibility = View.VISIBLE
                }
                is Status.Success -> {
                    binding.status.text = "Success"
                    binding.progress.visibility = View.INVISIBLE
                    binding.adviceTv.text = state.data?.slip?.advice ?: run {""}
                }
                is Status.Error -> {
                    binding.status.text = "Error"
                    binding.progress.visibility = View.INVISIBLE
                }
            }
        }

        binding.saveImg.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val bitmap = getScreenShotFromView(binding.rl)
                if (bitmap != null) {
                    imageViewModel.saveImage(bitmap)
                }
            }
            else {
                requestPermission {
                    val bitmap = getScreenShotFromView(binding.rl)
                    if (bitmap != null) {
                        imageViewModel.saveImage(bitmap)
                    }
                }
            }

        }

        binding.makeAdvice.setOnClickListener{
            imageViewModel.getImage()
            imageViewModel.getAdvice()
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