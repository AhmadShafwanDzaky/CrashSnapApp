package com.capstone.crashsnap.ui.tips

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.crashsnap.R
import com.capstone.crashsnap.databinding.ActivityResultBinding
import com.capstone.crashsnap.databinding.ActivityTipsBinding

class TipsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTipsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}