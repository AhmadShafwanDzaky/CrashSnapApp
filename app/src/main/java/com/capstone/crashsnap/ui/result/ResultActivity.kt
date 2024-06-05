package com.capstone.crashsnap.ui.result

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.crashsnap.R
import com.capstone.crashsnap.databinding.ActivityResultBinding
import com.capstone.crashsnap.ui.main.MainActivity
import com.capstone.crashsnap.ui.profile.ProfileActivity

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResultBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_STRING))
        binding.previewImageView.setImageURI(imageUri)


        binding.saveResultButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }



    companion object {
        const val EXTRA_IMAGE_STRING = "extra_image_string"
    }
}