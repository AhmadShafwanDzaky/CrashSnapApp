package com.capstone.crashsnap.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.crashsnap.R
import com.capstone.crashsnap.databinding.ActivityHistoryListBinding

class HistoryListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}