package com.capstone.crashsnap.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.capstone.crashsnap.R
import com.capstone.crashsnap.databinding.ActivityMainBinding
import com.capstone.crashsnap.getImageUri
import com.capstone.crashsnap.ui.history.HistoryListActivity
import com.capstone.crashsnap.ui.maps.MapsActivity
import com.capstone.crashsnap.ui.profile.ProfileActivity
import com.capstone.crashsnap.ui.result.ResultActivity
import com.capstone.crashsnap.ui.result.ResultActivity.Companion.EXTRA_IMAGE_STRING
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentImageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        binding.historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryListActivity::class.java))
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
        binding.fab.setOnClickListener {
            showBottomSheet()
        }

        binding.repairShopButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }


    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(EXTRA_IMAGE_STRING, uri.toString())
            startActivity(intent)
        } else {
            showToast(getString(R.string.no_media))
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(EXTRA_IMAGE_STRING, currentImageUri.toString())
            startActivity(intent)
        }
    }
    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast(getString(R.string.permission_success))
            } else {
                showToast(getString(R.string.permission_err))
            }
        }


    private fun showBottomSheet(){
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet,null)
        val dialog = BottomSheetDialog(this)
        val galleryGroup = dialogView.findViewById<View>(R.id.galleryGroup)
        val cameraGroup =  dialogView.findViewById<View>(R.id.cameraGroup)
        galleryGroup.setOnClickListener{
            startGallery()
            dialog.dismiss()
        }
        cameraGroup.setOnClickListener{
            startCamera()
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE

        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}