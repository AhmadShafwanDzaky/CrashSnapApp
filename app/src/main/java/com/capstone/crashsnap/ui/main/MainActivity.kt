package com.capstone.crashsnap.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.capstone.crashsnap.R
import com.capstone.crashsnap.databinding.ActivityMainBinding
import com.capstone.crashsnap.ui.auth.SignupActivity
import com.capstone.crashsnap.ui.history.HistoryListActivity
import com.capstone.crashsnap.ui.maps.MapsActivity
import com.capstone.crashsnap.ui.profile.ProfileActivity
import com.capstone.crashsnap.ui.result.ResultActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

    private fun showBottomSheet(){
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet,null)
        val dialog = BottomSheetDialog(this)
        val galleryGroup = dialogView.findViewById<View>(R.id.galleryGroup)
        val cameraGroup =  dialogView.findViewById<View>(R.id.cameraGroup)
        galleryGroup.setOnClickListener{
            // Open gallery
//            startGallery()
            startActivity(Intent(this, ResultActivity::class.java))
            dialog.dismiss()
        }
        cameraGroup.setOnClickListener{
            // Open camera
//            startCamera()
            startActivity(Intent(this, ResultActivity::class.java))
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }
}