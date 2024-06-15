package com.capstone.crashsnap.ui.preview

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.crashsnap.R
import com.capstone.crashsnap.databinding.ActivityPreviewBinding

class PreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.previewImageView.setImageURI(Uri.parse(intent.getStringExtra(EXTRA_IMAGE_STRING)))

    }

    companion object {
        const val EXTRA_IMAGE_STRING = "extra_image_string"
    }
}