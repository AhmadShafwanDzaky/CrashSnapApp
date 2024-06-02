package com.capstone.crashsnap.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.capstone.crashsnap.R
import com.capstone.crashsnap.databinding.ActivityProfileBinding
import com.capstone.crashsnap.ui.result.ResultActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEditImage.setOnClickListener {
            showBottomSheet()
        }

        binding.btnLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun showBottomSheet(){
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet,null)
        val dialog = BottomSheetDialog(this)
        val galleryGroup = dialogView.findViewById<View>(R.id.galleryGroup)
        val cameraGroup =  dialogView.findViewById<View>(R.id.cameraGroup)
        galleryGroup.setOnClickListener{
            // Open gallery
//            startGallery()
            dialog.dismiss()
        }
        cameraGroup.setOnClickListener{
            // Open camera
//            startCamera()
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }
}